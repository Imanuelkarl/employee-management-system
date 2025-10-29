package ng.darum.employee.service;

import ng.darum.employee.entity.Department;
import ng.darum.employee.repository.DepartmentRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Test class for DepartmentService
 * Uses Mockito to mock dependencies and test service layer logic
 */
@ExtendWith(MockitoExtension.class)
class DepartmentServiceTest {

    // Mock the repository dependency
    @Mock
    private DepartmentRepository departmentRepository;

    // Inject mocks into the service being tested
    @InjectMocks
    private DepartmentService departmentService;

    /**
     * Test createDepartment method when department does not exist
     * Verifies that department is saved with slug and returned
     */
    @Test
    void testCreateDepartment_WhenDepartmentDoesNotExist_ShouldSaveAndReturnDepartment() {
        // Arrange
        Department inputDepartment = new Department();
        inputDepartment.setName("Engineering");
        inputDepartment.setDescription("Software development department");

        Department savedDepartment = new Department();
        savedDepartment.setId(1L);
        savedDepartment.setName("I.C.T");
        savedDepartment.setSlug("i.c.t");
        savedDepartment.setDescription("Software development department");

        when(departmentRepository.existsByName("Engineering")).thenReturn(false);
        when(departmentRepository.save(any(Department.class))).thenReturn(savedDepartment);

        // Act
        Department result = departmentService.createDepartment(inputDepartment);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1L, result.getId(), "Department ID should match");
        assertEquals("I.C.T", result.getName(), "Department name should match");
        assertEquals("i.c.t", result.getSlug(), "Department slug should be generated");
        assertEquals("Software development department", result.getDescription(), "Department description should match");

        verify(departmentRepository, times(1)).existsByName("Engineering");
        verify(departmentRepository, times(1)).save(inputDepartment);
    }

    /**
     * Test createDepartment method when department already exists
     * Verifies that RuntimeException is thrown
     */
    @Test
    void testCreateDepartment_WhenDepartmentAlreadyExists_ShouldThrowException() {
        // Arrange
        Department inputDepartment = new Department();
        inputDepartment.setName("HR");

        when(departmentRepository.existsByName("HR")).thenReturn(true);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> departmentService.createDepartment(inputDepartment));

        assertEquals("Department Already exists", exception.getMessage());
        verify(departmentRepository, times(1)).existsByName("HR");
        verify(departmentRepository, never()).save(any(Department.class));
    }

    /**
     * Test updateDepartment method when department exists
     * Verifies that department is updated with new properties
     */
    @Test
    void testUpdateDepartment_WhenDepartmentExists_ShouldUpdateAndReturnDepartment() {
        // Arrange
        Long departmentId = 1L;

        Department existingDepartment = new Department();
        existingDepartment.setId(departmentId);
        existingDepartment.setName("Old Name");
        existingDepartment.setDescription("Old Description");
        existingDepartment.setSlug("old-name");

        Department updateData = new Department();
        updateData.setName("New Name");
        updateData.setDescription("New Description");
        // Note: slug is not set in updateData, so it should remain unchanged

        Department updatedDepartment = new Department();
        updatedDepartment.setId(departmentId);
        updatedDepartment.setName("New Name");
        updatedDepartment.setDescription("New Description");
        updatedDepartment.setSlug("old-name"); // Slug remains same

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.save(existingDepartment)).thenReturn(updatedDepartment);

        // Act
        Department result = departmentService.updateDepartment(departmentId, updateData);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(departmentId, result.getId(), "Department ID should match");
        assertEquals("New Name", result.getName(), "Department name should be updated");
        assertEquals("New Description", result.getDescription(), "Department description should be updated");
        assertEquals("old-name", result.getSlug(), "Department slug should remain unchanged");

        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).save(existingDepartment);
    }

    /**
     * Test updateDepartment method when department does not exist
     * Verifies that RuntimeException is thrown
     */
    @Test
    void testUpdateDepartment_WhenDepartmentDoesNotExist_ShouldThrowException() {
        // Arrange
        Long departmentId = 999L;
        Department updateData = new Department();
        updateData.setName("Non-existent Department");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> departmentService.updateDepartment(departmentId, updateData));

        assertEquals("Department not found", exception.getMessage());
        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, never()).save(any(Department.class));
    }

    /**
     * Test updateDepartment with partial update (null properties should be ignored)
     * Verifies that only non-null properties are updated
     */
    @Test
    void testUpdateDepartment_WithPartialUpdate_ShouldUpdateOnlyNonNullProperties() {
        // Arrange
        Long departmentId = 1L;

        Department existingDepartment = new Department();
        existingDepartment.setId(departmentId);
        existingDepartment.setName("Original Name");
        existingDepartment.setDescription("Original Description");
        existingDepartment.setSlug("original-name");

        Department updateData = new Department();
        updateData.setName("Updated Name");
        // Description is null, so it should remain unchanged

        Department updatedDepartment = new Department();
        updatedDepartment.setId(departmentId);
        updatedDepartment.setName("Updated Name");
        updatedDepartment.setDescription("Original Description"); // Unchanged
        updatedDepartment.setSlug("original-name"); // Unchanged

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(existingDepartment));
        when(departmentRepository.save(existingDepartment)).thenReturn(updatedDepartment);

        // Act
        Department result = departmentService.updateDepartment(departmentId, updateData);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("Updated Name", result.getName(), "Name should be updated");
        assertEquals("Original Description", result.getDescription(), "Description should remain unchanged");
        assertEquals("original-name", result.getSlug(), "Slug should remain unchanged");

        verify(departmentRepository, times(1)).findById(departmentId);
        verify(departmentRepository, times(1)).save(existingDepartment);
    }

    /**
     * Test deleteDepartment method when department exists
     * Verifies that department is deleted successfully
     */
    @Test
    void testDeleteDepartment_WhenDepartmentExists_ShouldDeleteDepartment() {
        // Arrange
        Long departmentId = 1L;

        when(departmentRepository.existsById(departmentId)).thenReturn(true);
        doNothing().when(departmentRepository).deleteById(departmentId);

        // Act
        departmentService.deleteDepartment(departmentId);

        // Assert
        verify(departmentRepository, times(1)).existsById(departmentId);
        verify(departmentRepository, times(1)).deleteById(departmentId);
    }

    /**
     * Test deleteDepartment method when department does not exist
     * Verifies that RuntimeException is thrown
     */
    @Test
    void testDeleteDepartment_WhenDepartmentDoesNotExist_ShouldThrowException() {
        // Arrange
        Long departmentId = 999L;

        when(departmentRepository.existsById(departmentId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> departmentService.deleteDepartment(departmentId));

        assertEquals("Department does not exist", exception.getMessage());
        verify(departmentRepository, times(1)).existsById(departmentId);
        verify(departmentRepository, never()).deleteById(any(Long.class));
    }

    /**
     * Test findDepartmentById method when department exists
     * Verifies that department is returned
     */
    @Test
    void testFindDepartmentById_WhenDepartmentExists_ShouldReturnDepartment() {
        // Arrange
        Long departmentId = 1L;
        Department expectedDepartment = new Department();
        expectedDepartment.setId(departmentId);
        expectedDepartment.setName("Finance");
        expectedDepartment.setSlug("finance");

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(expectedDepartment));

        // Act
        Department result = departmentService.findDepartmentById(departmentId);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(departmentId, result.getId(), "Department ID should match");
        assertEquals("Finance", result.getName(), "Department name should match");

        verify(departmentRepository, times(1)).findById(departmentId);
    }

    /**
     * Test findDepartmentById method when department does not exist
     * Verifies that RuntimeException is thrown
     */
    @Test
    void testFindDepartmentById_WhenDepartmentDoesNotExist_ShouldThrowException() {
        // Arrange
        Long departmentId = 999L;

        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> departmentService.findDepartmentById(departmentId));

        assertEquals("Department not found", exception.getMessage());
        verify(departmentRepository, times(1)).findById(departmentId);
    }

    /**
     * Test getAllDepartments method when departments exist
     * Verifies that list of departments is returned
     */
    @Test
    void testGetAllDepartments_WhenDepartmentsExist_ShouldReturnDepartmentList() {
        // Arrange
        Department dept1 = new Department();
        dept1.setId(1L);
        dept1.setName("HR");

        Department dept2 = new Department();
        dept2.setId(2L);
        dept2.setName("IT");

        List<Department> expectedDepartments = Arrays.asList(dept1, dept2);

        when(departmentRepository.findAll()).thenReturn(expectedDepartments);

        // Act
        List<Department> result = departmentService.getAllDepartments();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return 2 departments");
        assertEquals("HR", result.get(0).getName(), "First department name should match");
        assertEquals("IT", result.get(1).getName(), "Second department name should match");

        verify(departmentRepository, times(1)).findAll();
    }

    /**
     * Test getAllDepartments method when no departments exist
     * Verifies that empty list is returned
     */
    @Test
    void testGetAllDepartments_WhenNoDepartmentsExist_ShouldReturnEmptyList() {
        // Arrange
        List<Department> expectedDepartments = List.of();

        when(departmentRepository.findAll()).thenReturn(expectedDepartments);

        // Act
        List<Department> result = departmentService.getAllDepartments();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty list");

        verify(departmentRepository, times(1)).findAll();
    }
}