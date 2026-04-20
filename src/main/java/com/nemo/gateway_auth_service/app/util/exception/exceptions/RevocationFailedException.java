package com.nemo.gateway_auth_service.app.util.exception.exceptions;

public class RevocationFailedException extends RuntimeException {

    public RevocationFailedException(String message) {
        super(message);
    }
}
