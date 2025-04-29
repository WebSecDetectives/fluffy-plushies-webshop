package com.dlshomies.fluffyplushies.exception;

public class UserNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "User not found";

    public UserNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public UserNotFoundException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
