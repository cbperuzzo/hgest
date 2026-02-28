package com.lumem.hgest.model.util.err;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
