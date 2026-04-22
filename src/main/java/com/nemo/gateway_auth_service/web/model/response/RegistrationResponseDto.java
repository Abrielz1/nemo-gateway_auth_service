package com.nemo.gateway_auth_service.web.model.response;

import java.util.List;

public record RegistrationResponseDto(String userId, List<String> loginsList) {
}
