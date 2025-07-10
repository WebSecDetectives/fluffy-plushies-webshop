package com.sirmeows.fluffyinventoryservice.exception;

import java.util.UUID;

public class ItemNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Item not found";
    private static final String ID_MESSAGE = "Item with id '%s' does not exist";

    public ItemNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ItemNotFoundException(UUID id) {
        super(String.format(ID_MESSAGE, id));
    }

    public ItemNotFoundException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}