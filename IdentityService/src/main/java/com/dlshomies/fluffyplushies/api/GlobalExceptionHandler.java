package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.exception.DataInitializationException;
import com.dlshomies.fluffyplushies.exception.UnexpectedUserTypeException;
import com.dlshomies.fluffyplushies.exception.UserDeletedException;
import com.dlshomies.fluffyplushies.exception.UserNotFoundException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

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

    @ExceptionHandler(DataInitializationException.class)
    public ResponseEntity<ErrorResponse> handleDataInitializationException(DataInitializationException ex) {
        log.error("Data initialization failed", ex);
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(INITIALIZATION_ERROR, GENERIC_ERROR_MESSAGE));
    }

    @ExceptionHandler(UnexpectedUserTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedUserTypeException(final UnexpectedUserTypeException ex) {
        log.error("Unexpected user type encountered", ex);
        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(INTERNAL_SERVER_ERROR, GENERIC_ERROR_MESSAGE));
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(UserNotFoundException ex) {
        log.debug("User not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(UserDeletedException.class)
    public ResponseEntity<ErrorResponse> handleUserDeleted(final UserDeletedException ex) {
        log.debug("Attempted to access deleted user: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.LOCKED)
                .body(new ErrorResponse(LOCKED, ex.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(BindException ex) {
        log.debug("Validation failed: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        log.debug("Bad request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(BAD_REQUEST, GENERIC_VALIDATION_MESSAGE));
    }
}