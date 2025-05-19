package com.dlshomies.fluffyplushies.exception;

public class UnexpectedUserTypeException extends RuntimeException {
    private static final String MESSAGE_TEMPLATE = "Unexpected user type '%s'";

    public UnexpectedUserTypeException(String userType) {
        super(String.format(MESSAGE_TEMPLATE, userType));
    }

    public UnexpectedUserTypeException(String userType, Throwable cause) {
        super(String.format(MESSAGE_TEMPLATE, userType), cause);
    }
}
