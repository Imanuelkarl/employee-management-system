package ng.darum.employee.controllers;

import jakarta.servlet.http.HttpServletRequest;
import ng.darum.employee.entity.Department;
import ng.darum.employee.service.DepartmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Department controller handling CRUD operations for departments.
 * Extends BaseController to inherit standardized response handling.
 */
@RestController
@RequestMapping("/departments")
public class DepartmentController extends BaseController {

    @Autowired
    private DepartmentService departmentService;

    /**
     * Creates a new department
     */
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> createDepartment(@RequestBody Department department, HttpServletRequest request) {
        try {
            Department response = departmentService.createDepartment(department);
            return buildCreated("Department created successfully", response);
        } catch (Exception e) {
            return handleException(e, request, "CREATE_DEPARTMENT");
        }
    }

    /**
     * Updates an existing department by ID
     */
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateDepartment(@PathVariable Long id, @RequestBody Department department,
                                              HttpServletRequest request) {
        try {
            Department response = departmentService.updateDepartment(id, department);
            return buildSuccess("Department updated successfully", response);
        } catch (Exception e) {
            return handleException(e, request, "UPDATE_DEPARTMENT");
        }
    }

    /**
     * Deletes a department by ID
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> deleteDepartment(@PathVariable Long id, HttpServletRequest request) {
        try {
            departmentService.deleteDepartment(id);
            return buildNoContent("Department deleted successfully");
        } catch (Exception e) {
            return handleException(e, request, "DELETE_DEPARTMENT");
        }
    }

    /**
     * Finds a department by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> findDepartment(@PathVariable Long id, HttpServletRequest request) {
        try {
            Department entity = departmentService.findDepartmentById(id);
            return buildSuccess("Department fetched successfully", entity);
        } catch (Exception e) {
            return handleException(e, request, "FIND_DEPARTMENT_BY_ID");
        }
    }

    /**
     * Retrieves all departments
     */
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> getAllDepartments(HttpServletRequest request) {
        try {
            return buildSuccess("Departments retrieved successfully", departmentService.getAllDepartments());
        } catch (Exception e) {
            return handleException(e, request, "GET_ALL_DEPARTMENTS");
        }
    }
}