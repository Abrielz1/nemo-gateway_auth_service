package com.nemo.gateway_auth_service.app.util.exception.exceptions;

public class InvalidJwtAuthenticationException extends RuntimeException {

    public InvalidJwtAuthenticationException(String message) {
        super(message);
    }
}
