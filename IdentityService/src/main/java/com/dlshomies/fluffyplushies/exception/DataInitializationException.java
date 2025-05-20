package com.dlshomies.fluffyplushies.exception;

public class DataInitializationException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Failed to initialize application data";

    public DataInitializationException(String message, Throwable cause) {
        super(message, cause);
    }

    public DataInitializationException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
