package com.nemo.gateway_auth_service.app.service.orchestration.worker.impl;

import com.nemo.gateway_auth_service.app.domain.entity.child.Client;
import com.nemo.gateway_auth_service.app.domain.entity.parent.PasswordData;
import com.nemo.gateway_auth_service.app.repository.ClientRepository;
import com.nemo.gateway_auth_service.app.security.service.JwtTokenProvider;
import com.nemo.gateway_auth_service.app.service.orchestration.worker.ClientLoginWorker;
import com.nemo.gateway_auth_service.app.service.orchestration.worker.ClientSessionWorker;
import com.nemo.gateway_auth_service.app.service.strategy.ClientLookupStrategy;
import com.nemo.gateway_auth_service.app.service.strategy.impl.EmailStrategyImpl;
import com.nemo.gateway_auth_service.app.service.strategy.impl.LoginStrategyImpl;
import com.nemo.gateway_auth_service.app.service.strategy.impl.PhoneStrategyImpl;
import com.nemo.gateway_auth_service.web.model.request.ClientLoginRequestDTO;
import com.nemo.gateway_auth_service.web.model.request.RefreshTokenRequestDto;
import com.nemo.gateway_auth_service.web.model.response.AuthTokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class ClientLoginWorkerImpl implements ClientLoginWorker {

    private final ClientRepository clientRepository;

    private final PasswordEncoder passwordEncoder;

    private final List<ClientLookupStrategy> lookupStrategies;

    private final ClientSessionWorker clientSessionWorker;

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    @Transactional(readOnly = true)
    public AuthTokenDto login(ClientLoginRequestDTO loginRequest) {

        String id = loginRequest.clientIdentifier();

        ClientLookupStrategy strategyCurrent = this.lookupStrategies.stream()
                .filter(strategy -> strategy.supports(id))
                .findFirst()
                .orElseThrow(() -> new BadCredentialsException("Invalid data"));

        Optional<Client> client = Optional.empty();

      if (strategyCurrent instanceof EmailStrategyImpl) {
          client = this.clientRepository.findByEmail(id);
      }

      if (strategyCurrent instanceof PhoneStrategyImpl) {
          client = this.clientRepository.findByPhone(id);
      }

      if (strategyCurrent instanceof LoginStrategyImpl) {
          client = this.clientRepository.findByLogin(id);
      }

        Client clientFromDb = client.orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

      String passwordFromLogin = loginRequest.password();
      String passwordFromDB = clientFromDb.getUserPasswords().stream()
              .filter(PasswordData::getIsActive)
              .findFirst()
              .map(PasswordData::getPassword)
              .orElseThrow(() -> new BadCredentialsException("Invalid username or password"));

        if (!this.passwordEncoder.matches(passwordFromLogin, passwordFromDB)) {
            log.error("someone tries to pickup a password");
            throw new BadCredentialsException("Invalid username or password");
        }

        var sessionId = UUID.randomUUID();
        var now = Instant.now();
        var accessToken = this.jwtTokenProvider.accessTokenGenerator(clientFromDb.getUserUUID(), sessionId, now);
    var refreshToken = this.jwtTokenProvider.refreshTokenGenerator(clientFromDb.getUserUUID(), sessionId, now);

    this.clientSessionWorker.saveKeysToRedis(clientFromDb.getUserUUID(), accessToken, refreshToken);

      return new AuthTokenDto(accessToken, refreshToken, clientFromDb.getUserUUID().toString());
    }

    @Override
    public AuthTokenDto refresh(RefreshTokenRequestDto refreshTokenRequestDto) {
        var claims = this.jwtTokenProvider.getClaimsFromToken(refreshTokenRequestDto.refreshToken());
        var tokenType = claims.get("token_type", String.class);

        if (!StringUtils.hasText(tokenType) ||!Objects.equals("Refresh", tokenType)) {
            log.error("Attempt to refresh with invalid token type: {}", tokenType);
            throw new BadCredentialsException("Invalid token type");
        }

        var userId = UUID.fromString(claims.getSubject());
        var sessionId = UUID.fromString(claims.getId());
        var tokenFromRedis = this.clientSessionWorker.getRefreshTokenFromRedis(userId);

        if (!StringUtils.hasText(tokenFromRedis)) {
            log.error("Attempt to refresh with invalid token: {}", tokenFromRedis);
            throw new BadCredentialsException("Invalid token type");
        }

        if (!Objects.equals(tokenFromRedis, refreshTokenRequestDto.refreshToken())) {
            this.clientSessionWorker.deleteSession(userId);
            log.error("Token hijack attempt detected for user: {}!", userId);
            throw new BadCredentialsException("Session Expired");
        }

        var now = Instant.now();
        var newAccessToken = this.jwtTokenProvider.accessTokenGenerator(userId, sessionId, now);
        var newRefreshToken = this.jwtTokenProvider.refreshTokenGenerator(userId, sessionId, now);

        this.clientSessionWorker.saveKeysToRedis(userId, newAccessToken, newRefreshToken);

        return new AuthTokenDto(newAccessToken, newRefreshToken, userId.toString());
    }
}
