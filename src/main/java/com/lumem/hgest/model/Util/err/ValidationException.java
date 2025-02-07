package com.lumem.hgest.model.Util.err;

public class ValidationException extends RuntimeException {
    public ValidationException(String message) {
        super(message);
    }
}
