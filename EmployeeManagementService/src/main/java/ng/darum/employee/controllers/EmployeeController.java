package ng.darum.employee.controllers;

import jakarta.servlet.http.HttpServletRequest;
import ng.darum.employee.component.JwtUtil;
import ng.darum.employee.dto.EmployeeRequest;
import ng.darum.employee.entity.Employee;
import ng.darum.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * Employee controller handling CRUD operations for employees.
 * Extends BaseController to inherit standardized response handling.
 */
@RestController
@RequestMapping("/employees")
public class EmployeeController extends BaseController {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private JwtUtil jwtUtil;
    /**
     * Creates a new employee
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createEmployee(@RequestBody EmployeeRequest request, HttpServletRequest httpRequest) {
        try {
            Employee response = employeeService.createEmployee(request);
            return buildCreated("Employee created successfully", response);
        } catch (Exception e) {
            return handleException(e, httpRequest, "CREATE_EMPLOYEE");
        }
    }

    /**
     * Updates an existing employee by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody EmployeeRequest employee,
                                            HttpServletRequest request) {
        try {
            Object response = employeeService.updateEmployee(id, employee);
            return buildSuccess("Employee updated successfully", response);
        } catch (Exception e) {
            return handleException(e, request, "UPDATE_EMPLOYEE");
        }
    }

    /**
     * Deletes an employee by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteEmployee(@PathVariable Long id, HttpServletRequest request) {
        try {
            employeeService.deleteEmployee(id);
            return buildNoContent("Employee deleted successfully");
        } catch (Exception e) {
            return handleException(e, request, "DELETE_EMPLOYEE");
        }
    }

    /**
     * Finds an employee by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findEmployee(@PathVariable Long id, HttpServletRequest request) {
        try {
            Object response = employeeService.findEmployeeById(id);
            return buildSuccess("Employee fetched successfully", response);
        } catch (Exception e) {
            return handleException(e, request, "FIND_EMPLOYEE_BY_ID");
        }
    }

    /**
     * Retrieves all employees
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllEmployees(HttpServletRequest request) {
        try {
            return buildSuccess("Employees retrieved successfully", employeeService.getAllEmployees());
        } catch (Exception e) {
            return handleException(e, request, "GET_ALL_EMPLOYEES");
        }
    }

    /**
     * Retrieves employees by department ID
     */
    @GetMapping("/department/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
    public ResponseEntity<?> getDepartmentEmployees(@PathVariable Long id, HttpServletRequest request) {
        try {
            /*String userEmail = jwtUtil.extractEmail(extractToken(request));    // extract role from JWT claims
            String role = jwtUtil.extractRole(extractToken(request));
            Employee employee =employeeService.findEmployeeByEmail(userEmail);
            if(!Objects.equals(employee.getDepartmentId(), id)||!role.equalsIgnoreCase("ADMIN")){
                throw new IllegalAccessException("You can't access this department Info");
            }*/

            return buildSuccess("Department employees retrieved successfully",
                    employeeService.getDepartmentEmployees(id));

        } catch (Exception e) {
            return handleException(e, request, "GET_DEPARTMENT_EMPLOYEES");
        }
    }
    public String extractToken(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header != null && header.startsWith("Bearer ")) {
            return header.substring(7);
        }
        throw new RuntimeException("Missing or invalid Authorization header");
    }

}