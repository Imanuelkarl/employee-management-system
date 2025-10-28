package ng.darum.employee.controllers;

import jakarta.servlet.http.HttpServletRequest;
import ng.darum.employee.dto.EmployeeRequest;
import ng.darum.employee.dto.ErrorResponse;
import ng.darum.employee.dto.ServerResponse;
import ng.darum.employee.entity.Employee;
import ng.darum.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    EmployeeService employeeService;

    @PostMapping("/create")
    public ResponseEntity<ServerResponse<Object>> createEmployee(@RequestBody EmployeeRequest request){
        return ResponseEntity.ok(employeeService.createEmployee(request));
    }
    @PutMapping("/{id}")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id,@RequestBody  Employee employee, HttpServletRequest request){
        try {
            return ResponseEntity.ok(employeeService.updateEmployee(id,employee));
        }catch (Exception e){
            return handleException(e,request,"UPDATE_ENTITY");
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id,HttpServletRequest request){
        try {
            employeeService.deleteEmployee(id);
            return ResponseEntity.ok("Success");
        }catch (Exception e){
            return handleException(e,request,"DELETE_ENTITY");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> findEmployee(@PathVariable Long id,HttpServletRequest request){
        try {
            return ResponseEntity.ok(employeeService.findEmployeeById(id));
        }catch (Exception e){
            return handleException(e,request,"FIND_ENTITY_BY_ID");
        }
    }

    @GetMapping
    public ResponseEntity<?> getAllEmployee(HttpServletRequest request){

        try {
            return ResponseEntity.ok(employeeService.getAllEmployees());
        }catch (Exception e){
            return handleException(e,request,"GET_ALL_ENTITY");
        }
    }
    private <T> ResponseEntity<ng.darum.commons.dto.ServerResponse<T>> buildSuccess(String message, T data, HttpStatus status) {
        ng.darum.commons.dto.ServerResponse<T> body = ng.darum.commons.dto.ServerResponse.<T>builder()
                .status("success")
                .message(message)
                .data(data)
                .timestamp(ZonedDateTime.now())
                .build();

        return new ResponseEntity<>(body, status);
    }
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
                errorId
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
        } else if (e instanceof java.util.NoSuchElementException) {
            return HttpStatus.NOT_FOUND;
        } else if (e instanceof UnsupportedOperationException) {
            return HttpStatus.NOT_IMPLEMENTED;
        } else if (e instanceof org.springframework.dao.DataIntegrityViolationException) {
            return HttpStatus.CONFLICT;
       /* } else if (e instanceof org.springframework.security.access.AccessDeniedException) {
            return HttpStatus.FORBIDDEN;*/
        } else {
            return HttpStatus.INTERNAL_SERVER_ERROR;
        }
    }

    /** Determine error code based on exception and operation */
    private String determineErrorCode(Exception e, String operation) {
        if (e instanceof IllegalArgumentException) {
            return "INVALID_INPUT";
        } else if (e instanceof java.util.NoSuchElementException) {
            return "RESOURCE_NOT_FOUND";
        } else if (e instanceof UnsupportedOperationException) {
            return "FEATURE_NOT_IMPLEMENTED";
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
