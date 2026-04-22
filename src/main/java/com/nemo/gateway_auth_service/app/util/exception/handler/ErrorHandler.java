package com.nemo.gateway_auth_service.app.util.exception.handler;

import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.server.advice.GrpcAdvice;
import net.devh.boot.grpc.server.advice.GrpcExceptionHandler;
import java.util.stream.Collectors;

@Slf4j
@GrpcAdvice
public class ErrorHandler {

    @GrpcExceptionHandler(ConstraintViolationException.class)
    public StatusRuntimeException handleValidationException(ConstraintViolationException ex) {

        var errorMessage = ex.getConstraintViolations().stream()
                .map(v -> v.getPropertyPath() + ";" + v.getMessage())
                .collect(Collectors.joining(", "));

        log.warn("[WARN] Validation failed: {}", errorMessage);
        return Status.INVALID_ARGUMENT
                .withDescription(errorMessage)
                .asRuntimeException();
    }
}

