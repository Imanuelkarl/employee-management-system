package ng.darum.employee.controllers;

import jakarta.servlet.http.HttpServletRequest;
import ng.darum.employee.dto.EmployeeRequest;
import ng.darum.employee.entity.Employee;
import ng.darum.employee.service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Employee controller handling CRUD operations for employees.
 * Extends BaseController to inherit standardized response handling.
 */
@RestController
@RequestMapping("/employees")
public class EmployeeController extends BaseController {

    @Autowired
    private EmployeeService employeeService;

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
    public ResponseEntity<?> updateEmployee(@PathVariable Long id, @RequestBody Employee employee,
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getDepartmentEmployees(@PathVariable Long id, HttpServletRequest request) {
        try {
            return buildSuccess("Department employees retrieved successfully",
                    employeeService.getDepartmentEmployees(id));
        } catch (Exception e) {
            return handleException(e, request, "GET_DEPARTMENT_EMPLOYEES");
        }
    }
}