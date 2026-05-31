package com.sirmeows.fluffyinventoryservice.security;

import java.util.UUID;

/**
 * Authenticated principal derived from the verified JWT.
 *
 * Exposed as the Spring Security principal so controllers can read the caller's
 * id and role via {@code @AuthenticationPrincipal AuthUser} without re-parsing
 * the token. The {@code id} is the JWT subject (the user's UUID in IdentityService)
 * and is the trusted source for ownership decisions such as an item's merchantId.
 */
public record AuthUser(UUID id, String username, Role role) {
}
