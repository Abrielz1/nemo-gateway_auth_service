package com.nemo.gateway_auth_service.app.util.exception.exceptions;

public class UserNotVerifiedException extends RuntimeException {

    public UserNotVerifiedException(String message) {
        super(message);
    }
}
