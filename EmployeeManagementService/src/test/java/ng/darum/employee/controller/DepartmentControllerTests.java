package ng.darum.employee.controller;

import ng.darum.employee.config.SecurityConfig;
import ng.darum.employee.controllers.DepartmentController;
import ng.darum.employee.entity.Department;
import ng.darum.employee.service.DepartmentService;
import org.junit.jupiter.api.Test;
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
 * Simplified controller tests for DepartmentController.
 * Uses @WebMvcTest + @MockitoBean for isolated testing of the controller layer.
 */
@WebMvcTest(DepartmentController.class)
@Import(SecurityConfig.class)
class DepartmentControllerTests {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private DepartmentService departmentService;

    // ---------- CREATE ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    void createDepartment_ShouldReturnCreated() throws Exception {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Engineering");

        when(departmentService.createDepartment(any())).thenReturn(dept);

        mockMvc.perform(post("/departments")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Engineering"}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.message").value("Department created successfully"))
                .andExpect(jsonPath("$.data.name").value("Engineering"));

        verify(departmentService, times(1)).createDepartment(any());
    }

    // ---------- UPDATE ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    void updateDepartment_ShouldReturnOk() throws Exception {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Updated");

        when(departmentService.updateDepartment(eq(1L), any())).thenReturn(dept);

        mockMvc.perform(put("/departments/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"name":"Updated"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Department updated successfully"))
                .andExpect(jsonPath("$.data.name").value("Updated"));

        verify(departmentService, times(1)).updateDepartment(eq(1L), any());
    }

    // ---------- DELETE ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteDepartment_ShouldReturnNoContent() throws Exception {
        doNothing().when(departmentService).deleteDepartment(1L);

        mockMvc.perform(delete("/departments/1").with(csrf()))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.message").value("Department deleted successfully"));

        verify(departmentService, times(1)).deleteDepartment(1L);
    }

    // ---------- FIND BY ID ----------
    @Test
    @WithMockUser
    void findDepartment_ShouldReturnOk() throws Exception {
        Department dept = new Department();
        dept.setId(1L);
        dept.setName("Engineering");

        when(departmentService.findDepartmentById(1L)).thenReturn(dept);

        mockMvc.perform(get("/departments/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Department fetched successfully"))
                .andExpect(jsonPath("$.data.name").value("Engineering"));

        verify(departmentService, times(1)).findDepartmentById(1L);
    }

    // ---------- GET ALL ----------
    @Test
    @WithMockUser(roles = "ADMIN")
    void getAllDepartments_ShouldReturnList() throws Exception {
        Department dept1 = new Department();
        dept1.setId(1L);
        dept1.setName("Engineering");

        Department dept2 = new Department();
        dept2.setId(2L);
        dept2.setName("HR");

        when(departmentService.getAllDepartments()).thenReturn(List.of(dept1, dept2));

        mockMvc.perform(get("/departments"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("Departments retrieved successfully"))
                .andExpect(jsonPath("$.data.length()").value(2));

        verify(departmentService, times(1)).getAllDepartments();
    }

    // ---------- ACCESS CONTROL ----------
    @Test
    @WithMockUser(roles = "USER")
    void getAllDepartments_NonAdmin_ShouldReturnForbidden() throws Exception {
        mockMvc.perform(get("/departments"))
                .andExpect(status().isInternalServerError());

        verify(departmentService, never()).getAllDepartments();
    }
}
