package ng.darum.employee.controllers;

import jakarta.servlet.http.HttpServletRequest;
import ng.darum.commons.dto.ServerResponse;
import ng.darum.employee.dto.ErrorResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * Base controller providing common functionality for all REST controllers.
 * Follows SOLID principles:
 * - Single Responsibility: Handles only response formatting and error handling
 * - Open/Closed: Extensible for specific controller needs without modification
 * - Liskov Substitution: Can be used interchangeably with any controller
 * - Interface Segregation: Focused on specific response-related concerns
 * - Dependency Inversion: Depends on abstractions (HttpServletRequest, HttpStatus)
 */
@RestController
public abstract class BaseController {

    /**
     * Builds a standardized success response
     *
     * @param <T> Type of the data payload
     * @param message Success message for the client
     * @param data Response data payload
     * @param status HTTP status code
     * @return ResponseEntity with standardized success format
     */
    protected <T> ResponseEntity<ServerResponse<T>> buildSuccess(String message, T data, HttpStatus status) {
        ServerResponse<T> body = ServerResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(ZonedDateTime.now())
                .build();

        return new ResponseEntity<>(body, status);
    }

    /**
     * Builds a success response with OK status (200)
     *
     * @param <T> Type of the data payload
     * @param message Success message
     * @param data Response data
     * @return ResponseEntity with OK status
     */
    protected <T> ResponseEntity<ServerResponse<T>> buildSuccess(String message, T data) {
        return buildSuccess(message, data, HttpStatus.OK);
    }

    /**
     * Builds a success response with CREATED status (201)
     *
     * @param <T> Type of the data payload
     * @param message Success message
     * @param data Response data
     * @return ResponseEntity with CREATED status
     */
    protected <T> ResponseEntity<ServerResponse<T>> buildCreated(String message, T data) {
        return buildSuccess(message, data, HttpStatus.CREATED);
    }

    /**
     * Builds a success response with NO_CONTENT status (204)
     *
     * @param message Success message
     * @return ResponseEntity with NO_CONTENT status
     */
    protected ResponseEntity<ServerResponse<Void>> buildNoContent(String message) {
        return buildSuccess(message, null, HttpStatus.NO_CONTENT);
    }

    /**
     * Handles exceptions and builds standardized error responses
     *
     * @param e Exception that occurred
     * @param request HTTP servlet request
     * @param operation Operation identifier for logging
     * @return ResponseEntity with standardized error format
     */
    protected ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request, String operation) {
        String errorId = UUID.randomUUID().toString();
        String path = request.getRequestURI();

        // Determine HTTP status based on exception type
        HttpStatus status = determineHttpStatus(e);
        String errorCode = determineErrorCode(e, operation);
        String userMessage = getUserFriendlyMessage(e);

        // Create error response
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                errorCode,
                userMessage,
                path,
                ZonedDateTime.now(),
                errorId
        );

        // Add additional details for debugging
        errorResponse.addDetail("operation", operation);
        errorResponse.addDetail("exceptionType", e.getClass().getSimpleName());

        // Log the error with all details
        logError(errorId, operation, path, e);

        return new ResponseEntity<>(errorResponse, status);
    }

    /**
     * Determines appropriate HTTP status based on exception type
     *
     * @param e Exception that occurred
     * @return Appropriate HTTP status code
     */
    private HttpStatus determineHttpStatus(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        } else if (e instanceof java.util.NoSuchElementException) {
            return HttpStatus.NOT_FOUND;
        } else if (e instanceof UnsupportedOperationException) {
            return HttpStatus.NOT_IMPLEMENTED;
        } else if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
            return HttpStatus.CONFLICT;
        } else if (e instanceof org.springframework.security.access.AccessDeniedException) {
            return HttpStatus.FORBIDDEN;
        } else if ("Access Denied".equalsIgnoreCase(e.getMessage())) {
            return HttpStatus.UNAUTHORIZED;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /**
     * Determines error code based on exception and operation context
     *
     * @param e Exception that occurred
     * @param operation Operation identifier
     * @return Standardized error code
     */
    private String determineErrorCode(Exception e, String operation) {
        if (e instanceof IllegalArgumentException) {
            return "INVALID_INPUT";
        } else if (e instanceof java.util.NoSuchElementException) {
            return "RESOURCE_NOT_FOUND";
        } else if (e instanceof UnsupportedOperationException) {
            return "FEATURE_NOT_IMPLEMENTED";
        } else if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
            return "DATA_CONFLICT";
        } else if (e instanceof org.springframework.security.access.AccessDeniedException) {
            return "ACCESS_DENIED";
        } else {
            return "INTERNAL_ERROR";
        }
    }

    /**
     * Extracts user-friendly message from exception
     *
     * @param e Exception that occurred
     * @return User-friendly error message
     */
    private String getUserFriendlyMessage(Exception e) {
        // Return the exception message if it exists and is meaningful
        if (e.getMessage() != null && !e.getMessage().isEmpty()) {
            return e.getMessage();
        }

        // Provide default messages based on exception type
        if (e instanceof java.util.NoSuchElementException) {
            return "Requested resource was not found";
        } else if (e instanceof IllegalArgumentException) {
            return "Invalid input provided";
        } else if (e instanceof org.springframework.security.access.AccessDeniedException) {
            return "Access denied to the requested resource";
        } else {
            return "An unexpected error occurred";
        }
    }

    /**
     * Logs errors with consistent format including correlation ID
     *
     * @param errorId Unique error identifier for correlation
     * @param operation Operation that failed
     * @param path Request path
     * @param e Exception that occurred
     */
    private void logError(String errorId, String operation, String path, Exception e) {
        System.err.printf("""
            ERROR [%s]
            Operation: %s
            Path: %s
            Exception: %s
            Message: %s
            StackTrace:
            """, errorId, operation, path, e.getClass().getSimpleName(), e.getMessage());

        e.printStackTrace();
    }
}