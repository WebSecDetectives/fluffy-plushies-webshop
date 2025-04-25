package com.dlshomies.fluffyplushies.api;

import com.dlshomies.fluffyplushies.exception.UnexpectedUserTypeException;
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

    @ExceptionHandler(UnexpectedUserTypeException.class)
    public ResponseEntity<ErrorResponse> handleUnexpectedUserTypeException(final UnexpectedUserTypeException e) {
        return ResponseEntity
                .internalServerError()
                .body(new ErrorResponse(INTERNAL_SERVER_ERROR, "")); // We don't want to leak information about the class type for security reasons
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(BindException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors()
                .forEach(error -> errors.put(error.getField(), error.getDefaultMessage()));
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler({IllegalArgumentException.class, HttpMessageNotReadableException.class})
    public ResponseEntity<ErrorResponse> handleBadRequest(Exception ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(BAD_REQUEST, ex.getMessage()));
    }
}