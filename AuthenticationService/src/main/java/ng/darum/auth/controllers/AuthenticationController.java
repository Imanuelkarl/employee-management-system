package ng.darum.auth.controllers;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import ng.darum.auth.dto.AuthResponse;
import ng.darum.auth.dto.UserRequest;
import ng.darum.auth.services.AuthenticationService;
import ng.darum.commons.dto.ErrorResponse;
import ng.darum.commons.dto.ServerResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@RestController
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;


    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody UserRequest request, HttpServletRequest httpRequest, HttpServletResponse response) {
        try {
            AuthResponse authResponse = authenticationService.loginUser(request);
            String accessToken = authResponse.getToken();
            ResponseCookie cookie = ResponseCookie.from("access_token", accessToken)
                    .httpOnly(true)
                    .secure(true)
                    .path("/")
                    .sameSite("Strict")
                    .maxAge(7 * 24 * 60 * 60)
                    .build();

            response.addHeader("Set-Cookie", cookie.toString());
            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(ServerResponse.builder().status("success").message("Login Successfully").data(authResponse).build());
        } catch (Exception e) {
            return handleException(e, httpRequest, "USER_LOGIN");
        }
    }


    // Exception handling methods
    private ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request, String operation) {
        String errorId = UUID.randomUUID().toString();
        String path = request.getRequestURI();

        // Determine HTTP status based on exception type
        HttpStatus status = determineHttpStatus(e);
        String errorCode = determineErrorCode(e, operation);
        String userMessage = e.getMessage();

        // Create error response
        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                errorCode,
                userMessage,
                path,
                ZonedDateTime.now(),
                errorId,
                null
        );

        // Add additional details for debugging
        errorResponse.addDetail("operation", operation);
        errorResponse.addDetail("exceptionType", e.getClass().getSimpleName());

        // Log the error with all details
        logError(errorId, operation, path, e);

        return new ResponseEntity<>(errorResponse, status);
    }

    /** Determine HTTP status based on exception type */
    private HttpStatus determineHttpStatus(Exception e) {
        if (e instanceof IllegalArgumentException) {
            return HttpStatus.BAD_REQUEST;
        } else if (e instanceof RuntimeException) {
            String message = e.getMessage();
            if (message != null) {
                if (message.contains("already exists") || message.contains("already exists with this email")) {
                    return HttpStatus.CONFLICT;
                } else if (message.contains("not found") || message.contains("No user") || message.contains("No User")) {
                    return HttpStatus.NOT_FOUND;
                } else if (message.contains("password") || message.contains("Incorrect")) {
                    return HttpStatus.UNAUTHORIZED;
                }
            }
            return HttpStatus.BAD_REQUEST;
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /** Determine error code based on exception and operation */
    private String determineErrorCode(Exception e, String operation) {
        if (e instanceof IllegalArgumentException) {
            return "INVALID_INPUT";
        } else if (e instanceof RuntimeException) {
            String message = e.getMessage();
            if (message != null) {
                if (message.contains("already exists")) {
                    return "USER_ALREADY_EXISTS";
                } else if (message.contains("not found") || message.contains("No user")) {
                    return "USER_NOT_FOUND";
                } else if (message.contains("password") || message.contains("Incorrect")) {
                    return "INVALID_CREDENTIALS";
                }
            }
            return "AUTHENTICATION_ERROR";
        } else {
            return "INTERNAL_ERROR";
        }
    }

    /** Log errors with consistent format */
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