package com.monalisamenezes.icompras.pedidos.model.exception;

import lombok.Getter;

@Getter
public class ValidationException extends RuntimeException {
    private final String field;
    private final String message;

    public ValidationException(String field, String message) {
        super(message);
        this.field = field;
        this.message = message;
    }
}
