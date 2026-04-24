package com.nemo.gateway_auth_service.app.security.configuration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nemo.gateway_auth_service.app.domain.dto.redis.RedisSessionData;
import io.grpc.Metadata;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.security.authentication.GrpcAuthenticationReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity(proxyTargetClass = true)
public class GrpcSecurityConfig {

    private final StringRedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    private static final Metadata.Key<String> AUTHORIZATION_KEY =
            Metadata.Key.of("Authorization", Metadata.ASCII_STRING_MARSHALLER);

    @Bean
    public GrpcAuthenticationReader grpcAuthenticationReader() {
        return (call, headers) -> {

            String authHeader = headers.get(AUTHORIZATION_KEY);

            if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {

                log.warn("Missing Bearer token in gRPC request");
                return null;
            }

            String token = authHeader.substring(7);

            String redisKey = "session:" + token;
            String sessionDataJson = redisTemplate.opsForValue().get(redisKey);

            if (!StringUtils.hasText(sessionDataJson)) {
                log.warn("Token not found in Redis, session is invalid or expired.");
                throw io.grpc.Status.UNAUTHENTICATED
                        .withDescription("Session expired or invalid")
                        .asRuntimeException();
            }

           String userId;
            List<SimpleGrantedAuthority> authorities;
            try {

            var redisSessionData = this.objectMapper.readValue(sessionDataJson, RedisSessionData.class);

            userId = redisSessionData.userId();
            authorities = redisSessionData.authorities().stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role.trim()))
                    .toList();

            } catch (JsonProcessingException e) {

                log.error("Bad JSON in Redis! Could not deserialize session data.", e);
                throw io.grpc.Status.UNAUTHENTICATED
                        .withDescription("Internal session data corrupted")
                        .asRuntimeException();
            }

            log.info("gRPC Auth success for user: {}", userId);

            return new UsernamePasswordAuthenticationToken(userId, null, authorities);
        };
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return authentication -> authentication;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(9);
    }
}
