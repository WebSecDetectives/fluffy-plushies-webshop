package com.dlshomies.fluffyplushies.exception;

/**
 * Thrown when an operation is attempted on a user account
 * that exists but has been soft-deleted.
 */
public class UserDeletedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "User account is deactivated.";

    public UserDeletedException() {
        super(DEFAULT_MESSAGE);
    }

    public UserDeletedException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
