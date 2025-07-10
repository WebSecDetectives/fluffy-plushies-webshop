package com.sirmeows.fluffyinventoryservice.exception;

import java.util.UUID;

public class ReviewNotFoundException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "Item not found";
    private static final String ID_MESSAGE = "Item with id '%s' does not exist";

    public ReviewNotFoundException(UUID id) {
        super(String.format(ID_MESSAGE, id));
    }

    public ReviewNotFoundException() {
        super(DEFAULT_MESSAGE);
    }

    public ReviewNotFoundException(Throwable cause) {
        super(DEFAULT_MESSAGE, cause);
    }
}
