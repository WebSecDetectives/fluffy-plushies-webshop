package com.sirmeows.fluffyinventoryservice.exception;

import java.util.UUID;

/**
 * Thrown when an authenticated caller may act on items in general but not on this
 * specific instance — e.g. a merchant trying to modify another merchant's item.
 * Maps to 403 Forbidden. (For an item the caller cannot even see, throw
 * {@link ItemNotFoundException} (404) instead, so its existence is not leaked.)
 */
public class ItemAccessDeniedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "You are not allowed to modify this item";
    private static final String ID_MESSAGE = "You are not allowed to modify item with id '%s'";

    public ItemAccessDeniedException() {
        super(DEFAULT_MESSAGE);
    }

    public ItemAccessDeniedException(UUID id) {
        super(String.format(ID_MESSAGE, id));
    }
}
