package ng.darum.employee.dto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {

        String errorId = UUID.randomUUID().toString();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_FOUND.value(), // Using 404 for "not found" cases
                "NOT_FOUND",
                ex.getMessage(),
                request.getDescription(false),
                ZonedDateTime.now(),
                errorId
        );

        log.error("IllegalArgumentException [{}] at path: {} - Message: {}",
                errorId, request.getDescription(false), ex.getMessage(), ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnsupportedOperationException.class)
    public ResponseEntity<ErrorResponse> handleUnsupportedOperationException(
            UnsupportedOperationException ex, WebRequest request) {

        String errorId = UUID.randomUUID().toString();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.NOT_IMPLEMENTED.value(),
                "NOT_IMPLEMENTED",
                ex.getMessage(),
                request.getDescription(false),
                ZonedDateTime.now(),
                errorId
        );

        log.error("UnsupportedOperationException [{}]: {}", errorId, ex.getMessage(), ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_IMPLEMENTED);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAllExceptions(
            Exception ex, WebRequest request) {

        String errorId = UUID.randomUUID().toString();

        ErrorResponse errorResponse = new ErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "INTERNAL_ERROR",
                "An unexpected error occurred",
                request.getDescription(false),
                ZonedDateTime.now(),
                errorId
        );

        log.error("Unexpected error [{}] at path: {}", errorId, request.getDescription(false), ex);

        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}