package com.sirmeows.fluffyinventoryservice.exception;

import java.util.UUID;

/**
 * Thrown when an authenticated caller tries to modify a review they don't own and isn't an
 * admin moderator. Maps to 403 Forbidden. (For a review that doesn't exist, throw
 * {@link ReviewNotFoundException} (404).)
 */
public class ReviewAccessDeniedException extends RuntimeException {
    private static final String DEFAULT_MESSAGE = "You are not allowed to modify this review";
    private static final String ID_MESSAGE = "You are not allowed to modify review with id '%s'";

    public ReviewAccessDeniedException() {
        super(DEFAULT_MESSAGE);
    }

    public ReviewAccessDeniedException(UUID id) {
        super(String.format(ID_MESSAGE, id));
    }
}
