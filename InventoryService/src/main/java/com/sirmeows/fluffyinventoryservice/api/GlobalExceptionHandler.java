package com.sirmeows.fluffyinventoryservice.api;

import com.sirmeows.fluffyinventoryservice.exception.ItemNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ErrorResponse(String error, String message) {}
    private static final String INTERNAL_SERVER_ERROR = "INTERNAL_SERVER_ERROR";
    private static final String BAD_REQUEST = "BAD_REQUEST";
    private static final String NOT_FOUND = "NOT_FOUND";
    private static final String LOCKED = "LOCKED";
    private static final String INITIALIZATION_ERROR = "INITIALIZATION_ERROR";

    private static final String GENERIC_ERROR_MESSAGE = "An internal server error occurred";
    private static final String GENERIC_SECURITY_MESSAGE = "Access denied";
    private static final String GENERIC_VALIDATION_MESSAGE = "Invalid input provided";

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleItemNotFound(final ItemNotFoundException ex) {
        log.debug("Item not found: {}",ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(NOT_FOUND, ex.getMessage()));
    }
}
