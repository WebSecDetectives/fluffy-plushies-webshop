package com.dlshomies.fluffyplushies.exception;

import java.util.UUID;

public class UserAlreadyExistsException extends RuntimeException {
    private static final String idMessage = "User with id '%s' already exists";
    private static final String fieldMessage = "User with %s '%s' already exists";

    public UserAlreadyExistsException(String id) {
        super(String.format(idMessage, id));
    }

    public UserAlreadyExistsException(UUID id) {
        super(String.format(idMessage, id.toString()));
    }

    public UserAlreadyExistsException(String field, String value) {
        super(String.format(fieldMessage, field, value));
    }

    public UserAlreadyExistsException(String id, Throwable cause) {
        super(String.format(idMessage, id), cause);
    }

    public UserAlreadyExistsException(UUID id, Throwable cause) {
        super(String.format(idMessage, id.toString()), cause);
    }

    public UserAlreadyExistsException(String field, String value, Throwable cause) {
        super(String.format(fieldMessage, field, value), cause);
    }
}
