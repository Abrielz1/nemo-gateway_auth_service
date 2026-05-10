package com.nemo.gateway_auth_service.app.security.service;

import com.nemo.gateway_auth_service.app.domain.dto.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class JwtTokenProviderImpl implements JwtTokenProvider {

    private final JwtProperties jwtProps;

    private static final String ISSUER = "nemo-auth-service";

    private SecretKey getSigningKey() {

        var baseArray = Decoders.BASE64.decode(this.jwtProps.secret());
        return Keys.hmacShaKeyFor(baseArray);
    }

    @Override
    public String accessTokenGenerator(UUID userId, UUID sessionId, Instant timestamp) {
        Instant expiration = timestamp.plus(this.jwtProps.expiration().access());
        return Jwts.builder()
                .subject(userId.toString())
                .issuer(ISSUER)
                .id(sessionId.toString())
                .issuedAt(Date.from(timestamp))
                .expiration(Date.from(expiration))
                .signWith(this.getSigningKey())
                .compact();
    }

    @Override
    public String refreshTokenGenerator(UUID userUUID, UUID sessionId, Instant now) {

        return Jwts.builder()
                .subject(userUUID.toString())
                .issuer(ISSUER)
                .id(sessionId.toString())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(this.jwtProps.expiration().refresh())))
                .claim("token_type", "Refresh")
                .signWith(this.getSigningKey())
                .compact();
    }

    @Override
    public Claims getClaimsFromToken(String token) {

        try {
            return Jwts.parser()
                    .verifyWith(this.getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (JwtException e) {
            log.warn("Jwt token is invalid or expired: {}", e.getMessage());
            throw new BadCredentialsException("Invalid or expired token");
        }
    }
}
