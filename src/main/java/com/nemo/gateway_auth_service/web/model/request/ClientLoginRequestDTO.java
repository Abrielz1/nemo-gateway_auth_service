package com.nemo.gateway_auth_service.web.model.request;

import lombok.Builder;

@Builder
public record ClientLoginRequestDTO(String clientIdentifier, String password) {
}
