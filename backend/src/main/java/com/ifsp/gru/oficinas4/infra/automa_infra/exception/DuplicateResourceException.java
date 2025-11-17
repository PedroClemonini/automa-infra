package com.ifsp.gru.oficinas4.infra.automa_infra.exception;

public class DuplicateResourceException extends RuntimeException {
    public DuplicateResourceException(String message) {
        super(message);
    }
}
