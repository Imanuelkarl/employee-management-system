package ng.darum.employee.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import ng.darum.employee.config.SecurityConfig;
import ng.darum.employee.controllers.EmployeeController;
import ng.darum.employee.dto.EmployeeRequest;
import ng.darum.employee.entity.Employee;
import ng.darum.employee.enums.Role;
import ng.darum.employee.service.EmployeeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test class for {@link EmployeeController}.
 * Uses Mockito and MockMvc for isolated controller testing.
 */
@WebMvcTest(EmployeeController.class)
@Import(SecurityConfig.class)
class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmployeeService employeeService;

    @Autowired
    private ObjectMapper objectMapper;

    private Employee mockEmployee;
    private EmployeeRequest mockRequest;

    @BeforeEach
    void setUp() {
        mockEmployee = new Employee();
        mockEmployee.setId(1L);
        mockEmployee.setEmployeeId("EMP001");
        mockEmployee.setFirstName("John");
        mockEmployee.setLastName("Doe");
        mockEmployee.setStatus("ACTIVE");
        mockEmployee.setDepartmentId(10L);

        mockRequest = EmployeeRequest.builder()
                .email("john.doe@example.com")
                .password("password123")
                .role(Role.EMPLOYEE)
                .firstName("John")
                .lastName("Doe")
                .employeeId("EMP001")
                .status("ACTIVE")
                .departmentId(10L)
                .build();
    }

    // ---------------------------
    // CREATE EMPLOYEE
    // ---------------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldCreateEmployeeSuccessfully() throws Exception {
        Mockito.when(employeeService.createEmployee(any(EmployeeRequest.class)))
                .thenReturn(mockEmployee);

        mockMvc.perform(post("/employees")
                        .with(csrf()) // ✅ Required for POST
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Employee created successfully"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.lastName").value("Doe"))
                .andExpect(jsonPath("$.data.employeeId").value("EMP001"));
    }

    @Test
    void shouldRejectCreateEmployeeIfNotAuthorized() throws Exception {
        mockMvc.perform(post("/employees")
                        .with(csrf()) // ✅ Include CSRF here too for accurate rejection
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockRequest)))
                .andExpect(status().isUnauthorized());
    }

    // ---------------------------
    // UPDATE EMPLOYEE
    // ---------------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldUpdateEmployeeSuccessfully() throws Exception {
        Mockito.when(employeeService.updateEmployee(eq(1L), any(Employee.class)))
                .thenReturn(mockEmployee);

        mockMvc.perform(put("/employees/1")
                        .with(csrf()) // ✅ Required for PUT
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mockEmployee)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee updated successfully"))
                .andExpect(jsonPath("$.data.firstName").value("John"));
    }

    // ---------------------------
    // DELETE EMPLOYEE
    // ---------------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldDeleteEmployeeSuccessfully() throws Exception {
        Long employeeId = 1L;
        doNothing().when(employeeService).deleteEmployee(employeeId);

        mockMvc.perform(delete("/employees/{id}", employeeId)
                        .with(csrf())) // ✅ Correct
                .andExpect(status().isNoContent());

        verify(employeeService, times(1)).deleteEmployee(employeeId);
    }

    // ---------------------------
    // FIND EMPLOYEE BY ID
    // ---------------------------
    @Test
    @WithMockUser(roles = "USER")
    void shouldFindEmployeeByIdSuccessfully() throws Exception {
        Mockito.when(employeeService.findEmployeeById(1L))
                .thenReturn(mockEmployee);

        mockMvc.perform(get("/employees/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employee fetched successfully"))
                .andExpect(jsonPath("$.data.firstName").value("John"))
                .andExpect(jsonPath("$.data.employeeId").value("EMP001"));
    }

    // ---------------------------
    // GET ALL EMPLOYEES
    // ---------------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetAllEmployeesSuccessfully() throws Exception {
        Mockito.when(employeeService.getAllEmployees())
                .thenReturn(List.of(mockEmployee));

        mockMvc.perform(get("/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Employees retrieved successfully"))
                .andExpect(jsonPath("$.data[0].firstName").value("John"));
    }

    // ---------------------------
    // GET EMPLOYEES BY DEPARTMENT
    // ---------------------------
    @Test
    @WithMockUser(roles = "ADMIN")
    void shouldGetDepartmentEmployeesSuccessfully() throws Exception {
        Mockito.when(employeeService.getDepartmentEmployees(10L))
                .thenReturn(List.of(mockEmployee));

        mockMvc.perform(get("/employees/department/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Department employees retrieved successfully"))
                .andExpect(jsonPath("$.data[0].employeeId").value("EMP001"));
    }
}
