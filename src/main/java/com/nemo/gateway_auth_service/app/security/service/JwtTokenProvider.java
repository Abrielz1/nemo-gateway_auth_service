package com.nemo.gateway_auth_service.app.security.service;

import io.jsonwebtoken.Claims;
import java.time.Instant;
import java.util.UUID;

public interface JwtTokenProvider {

    String accessTokenGenerator(UUID userId, UUID sessionId, Instant timestamp);

    String refreshTokenGenerator(UUID userUUID, UUID sessionId, Instant now);

    Claims getClaimsFromToken(String token);
}
