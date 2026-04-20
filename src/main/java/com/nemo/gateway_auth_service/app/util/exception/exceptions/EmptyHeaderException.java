package com.nemo.gateway_auth_service.app.util.exception.exceptions;

public class EmptyHeaderException extends  RuntimeException{

    public EmptyHeaderException(String message) {
        super(message);
    }
}
