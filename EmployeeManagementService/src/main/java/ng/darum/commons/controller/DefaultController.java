package ng.darum.commons.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import ng.darum.commons.dto.ErrorResponse;
import ng.darum.commons.dto.ServerResponse;
import ng.darum.commons.service.DefaultService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

/**
 * ==========================================================
 * 🔹 DefaultController<E, ID, Req, Res>
 * ==========================================================
 * A reusable, generic REST controller with unified:
 * ✅ CRUD endpoints
 * ✅ Structured success responses
 * ✅ Centralized error handling
 *
 * Works with or without DTOs (automatic fallback to entities).
 */
@Slf4j
public abstract class DefaultController<E, ID, Req, Res> {

    protected final DefaultService<E, ID, Req, Res> service;

    protected DefaultController(DefaultService<E, ID, Req, Res> service) {
        this.service = service;
    }

    // =====================================================
    // 🔸 CREATE (Supports DTO or Entity)
    // =====================================================
    @PostMapping
    public ResponseEntity<?> create(@RequestBody Req dto, HttpServletRequest request) {
        try {
            Res response = service.createFromDto(dto);
            return buildSuccess("Entity created successfully", response, HttpStatus.CREATED);
        } catch (Exception e) {
            return handleException(e, request, "CREATE_ENTITY");
        }
    }

    // =====================================================
    // 🔸 READ ALL
    // =====================================================
    @GetMapping
    public ResponseEntity<?> getAll(HttpServletRequest request) {
        try {
            try {
                return buildSuccess("Data retrieved successfully", service.getAllAsDto(), HttpStatus.OK);
            } catch (UnsupportedOperationException e) {
                log.warn("DTO conversion not implemented, returning entities");
                return buildSuccess("Data retrieved successfully", service.getAll(), HttpStatus.OK);
            }
        } catch (Exception e) {
            return handleException(e, request, "GET_ALL");
        }
    }

    // =====================================================
    // 🔸 READ BY ID
    // =====================================================
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable ID id, HttpServletRequest request) {
        try {
            E entity = service.getById(id);
            try {
                return buildSuccess("Entity fetched successfully", service.toResponse(entity), HttpStatus.OK);
            } catch (UnsupportedOperationException e) {
                return buildSuccess("Entity fetched successfully", entity, HttpStatus.OK);
            }
        } catch (Exception e) {
            return handleException(e, request, "GET_BY_ID");
        }
    }

    // =====================================================
    // 🔸 UPDATE (Supports DTO or Entity)
    // =====================================================
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable ID id, @RequestBody Req dto, HttpServletRequest request) {
        try {
            Res response = service.updateFromDto(id, dto);
            return buildSuccess("Entity updated successfully", response, HttpStatus.OK);
        } catch (UnsupportedOperationException e) {
            log.warn("DTO update not implemented, using entity update");
            E entity = (E) dto;
            return buildSuccess("Entity updated successfully", service.update(id, entity), HttpStatus.OK);
        } catch (Exception e) {
            return handleException(e, request, "UPDATE_ENTITY");
        }
    }

    // =====================================================
    // 🔸 DELETE
    // =====================================================
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable ID id, HttpServletRequest request) {
        try {
            service.delete(id);
            return buildSuccess("Entity deleted successfully", null, HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return handleException(e, request, "DELETE_ENTITY");
        }
    }

    // =====================================================
    // ✅ SUCCESS RESPONSE WRAPPER
    // =====================================================

    /** Builds a standardized ServerResponse for successful operations */
    private <T> ResponseEntity<ServerResponse<T>> buildSuccess(String message, T data, HttpStatus status) {
        ServerResponse<T> body = ServerResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(ZonedDateTime.now())
                .build();

        return new ResponseEntity<>(body, status);
    }

    // =====================================================
    // ⚠️ ERROR HANDLING
    // =====================================================

    /** Unified error handler for all controller methods */
    private ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request, String operation) {
        String errorId = UUID.randomUUID().toString();
        String path = request.getRequestURI();

        HttpStatus status = determineHttpStatus(e);
        String errorCode = determineErrorCode(e, operation);
        String message = e.getMessage() != null ? e.getMessage() : "Unexpected error";

        ErrorResponse errorResponse = new ErrorResponse(
                status.value(),
                errorCode,
                message,
                path,
                ZonedDateTime.now(),
                errorId,
                null
        );

        errorResponse.addDetail("operation", operation);
        errorResponse.addDetail("exceptionType", e.getClass().getSimpleName());

        logError(errorId, operation, path, e);

        return new ResponseEntity<>(errorResponse, status);
    }

    /** Determines appropriate HTTP status from exception type */
    private HttpStatus determineHttpStatus(Exception e) {
        if (e instanceof IllegalArgumentException)
            return HttpStatus.BAD_REQUEST;
        if (e instanceof UnsupportedOperationException)
            return HttpStatus.NOT_IMPLEMENTED;
        if (e.getMessage() != null) {
            String msg = e.getMessage().toLowerCase();
            if (msg.contains("not found")) return HttpStatus.NOT_FOUND;
            if (msg.contains("unauthorized")) return HttpStatus.UNAUTHORIZED;
            if (msg.contains("exists") || msg.contains("duplicate")) return HttpStatus.CONFLICT;
        }
        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    /** Determines error code label */
    private String determineErrorCode(Exception e, String operation) {
        String msg = e.getMessage() != null ? e.getMessage().toLowerCase() : "";
        if (e instanceof IllegalArgumentException) return "INVALID_INPUT";
        if (msg.contains("not found")) return "RESOURCE_NOT_FOUND";
        if (msg.contains("exists")) return "RESOURCE_EXISTS";
        if (msg.contains("unauthorized")) return "UNAUTHORIZED";
        if (msg.contains("not implemented")) return "NOT_IMPLEMENTED";
        return operation + "_ERROR";
    }

    /** Logs all captured errors with contextual info */
    private void logError(String errorId, String operation, String path, Exception e) {
        log.error("""
        ERROR [{}]
        ├─ Operation: {}
        ├─ Path: {}
        ├─ Exception: {}
        └─ Message: {}
        """, errorId, operation, path, e.getClass().getSimpleName(), e.getMessage(), e);
    }

}
