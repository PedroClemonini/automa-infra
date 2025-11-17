package com.ifsp.gru.oficinas4.infra.automa_infra.exception;

public class UnauthorizedException extends RuntimeException {
    public UnauthorizedException(String message) {
        super(message);
    }
}
