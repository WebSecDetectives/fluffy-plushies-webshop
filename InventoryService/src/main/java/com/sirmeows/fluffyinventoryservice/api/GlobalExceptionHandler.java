package com.sirmeows.fluffyinventoryservice.api;

import com.sirmeows.fluffyinventoryservice.exception.InvalidImageException;
import com.sirmeows.fluffyinventoryservice.exception.ItemAccessDeniedException;
import com.sirmeows.fluffyinventoryservice.exception.ItemNotFoundException;
import com.sirmeows.fluffyinventoryservice.exception.ReviewAccessDeniedException;
import com.sirmeows.fluffyinventoryservice.exception.ReviewNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ErrorResponse(String error, String message) {}
    private static final String BAD_REQUEST = "BAD_REQUEST";
    private static final String NOT_FOUND = "NOT_FOUND";
    private static final String FORBIDDEN = "FORBIDDEN";

    private static final String GENERIC_SECURITY_MESSAGE = "Access denied";
    private static final String GENERIC_VALIDATION_MESSAGE = "Invalid input provided";

    @ExceptionHandler(ItemNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleItemNotFound(final ItemNotFoundException ex) {
        log.debug("Item not found: {}",ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ItemAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleItemAccessDenied(final ItemAccessDeniedException ex) {
        log.debug("Item access denied: {}", ex.getMessage());
        // Return a generic message; don't reveal item details or why access was denied.
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(FORBIDDEN, GENERIC_SECURITY_MESSAGE));
    }

    @ExceptionHandler(ReviewNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleReviewNotFound(final ReviewNotFoundException ex) {
        log.debug("Review not found: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(NOT_FOUND, ex.getMessage()));
    }

    @ExceptionHandler(ReviewAccessDeniedException.class)
    public ResponseEntity<ErrorResponse> handleReviewAccessDenied(final ReviewAccessDeniedException ex) {
        log.debug("Review access denied: {}", ex.getMessage());
        // Generic message; don't reveal review details or why access was denied.
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(FORBIDDEN, GENERIC_SECURITY_MESSAGE));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(BindException ex) {
        log.debug("Validation failed: {}", ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(InvalidImageException.class)
    public ResponseEntity<ErrorResponse> handleInvalidImage(final InvalidImageException ex) {
        log.debug("Invalid image upload: {}", ex.getMessage());
        // The message is safe to return: it describes the validation rule, not internals.
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(BAD_REQUEST, ex.getMessage()));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ErrorResponse> handleMaxUploadSize(final MaxUploadSizeExceededException ex) {
        log.debug("Upload too large: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.PAYLOAD_TOO_LARGE)
                .body(new ErrorResponse("PAYLOAD_TOO_LARGE", "Uploaded file is too large"));
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        log.debug("Bad request: {}", ex.getMessage());
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(BAD_REQUEST, GENERIC_VALIDATION_MESSAGE));
    }
}
