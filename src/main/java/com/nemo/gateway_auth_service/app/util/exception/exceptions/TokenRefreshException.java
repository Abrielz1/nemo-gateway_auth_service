package com.nemo.gateway_auth_service.app.util.exception.exceptions;

public class TokenRefreshException extends RuntimeException {

    public TokenRefreshException(String message) {
        super(message);
    }
}
