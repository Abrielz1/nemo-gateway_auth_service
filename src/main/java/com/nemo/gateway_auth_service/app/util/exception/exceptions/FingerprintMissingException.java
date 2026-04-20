package com.nemo.gateway_auth_service.app.util.exception.exceptions;

public class FingerprintMissingException extends RuntimeException {

    public FingerprintMissingException(String message) {
        super(message);
    }
}
