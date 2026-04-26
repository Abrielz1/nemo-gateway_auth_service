package com.nemo.gateway_auth_service.web.model.response;

public record AuthTokenDto(String accessToken, String refreshToken, String userId) {
}
