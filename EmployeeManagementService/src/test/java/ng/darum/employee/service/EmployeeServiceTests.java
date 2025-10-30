package ng.darum.employee.service;

import ng.darum.commons.dto.UserEvent;
import ng.darum.employee.dto.EmployeeRequest;
import ng.darum.employee.entity.Employee;
import ng.darum.employee.enums.Role;
import ng.darum.employee.repository.EmployeeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Test class for EmployeeService
 * Uses Mockito to mock dependencies and test service layer logic
 */
@ExtendWith(MockitoExtension.class)
class EmployeeServiceTest {

    // Mock the repository and kafka producer dependencies
    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private KafkaProducerService kafkaProducerService;

    // Inject mocks into the service being tested
    @InjectMocks
    private EmployeeService employeeService;

    // Captor for capturing Kafka events
    @Captor
    private ArgumentCaptor<UserEvent> userEventCaptor;

    /**
     * Test createEmployee method with valid EmployeeRequest
     * Verifies that employee is saved and Kafka event is published
     */
    @Test
    void testCreateEmployee_WithValidRequest_ShouldSaveEmployeeAndPublishEvent() {
        // Arrange
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFirstName("John");
        employeeRequest.setLastName("Doe");
        employeeRequest.setEmployeeId("EMP001");
        employeeRequest.setDepartmentId(1L);
        employeeRequest.setEmail("john.doe@company.com");
        employeeRequest.setPassword("securePassword");
        employeeRequest.setRole(Role.ADMIN);

        Employee employeeToSave = Employee.builder()
                .id(1L)
                .firstName("John")
                .lastName("Doe")
                .employeeId("EMP001")
                .departmentId(1L)
                .build();



        when(employeeRepository.save(any(Employee.class))).thenReturn(employeeToSave);
        doNothing().when(kafkaProducerService).publishUserCreatedEvent(any(UserEvent.class));

        // Act
        Employee result = employeeService.createEmployee(employeeRequest);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1L, result.getId(), "Employee ID should match");
        assertEquals("John", result.getFirstName(), "First name should match");
        assertEquals("Doe", result.getLastName(), "Last name should match");
        assertEquals("EMP001", result.getEmployeeId(), "Employee ID should match");
        assertEquals(1L, result.getDepartmentId(), "Department ID should match");

        // Verify Kafka event was published with correct data
        verify(kafkaProducerService, times(1)).publishUserCreatedEvent(userEventCaptor.capture());
        UserEvent capturedEvent = userEventCaptor.getValue();
        assertEquals("john.doe@company.com", capturedEvent.getEmail(), "Email should match");
        assertEquals("securePassword", capturedEvent.getPassword(), "Password should match");
        assertEquals(Role.ADMIN, capturedEvent.getRole(), "Role should match");

        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    /**
     * Test createEmployee method with partial EmployeeRequest data
     * Verifies that employee is saved with available data
     */
    @Test
    void testCreateEmployee_WithPartialData_ShouldSaveEmployee() {
        // Arrange
        EmployeeRequest employeeRequest = new EmployeeRequest();
        employeeRequest.setFirstName("Jane");
        employeeRequest.setLastName("Smith");
        employeeRequest.setEmployeeId("EMP002");
        employeeRequest.setDepartmentId(2L);


        Employee savedEmployee = new Employee();
        savedEmployee.setId(1L);
        savedEmployee.setFirstName("Jane");
        savedEmployee.setLastName("Smith");
        savedEmployee.setEmployeeId("EMP002");
        savedEmployee.setDepartmentId(2L);

        when(employeeRepository.save(any(Employee.class))).thenReturn(savedEmployee);
        doNothing().when(kafkaProducerService).publishUserCreatedEvent(any(UserEvent.class));

        // Act
        Employee result = employeeService.createEmployee(employeeRequest);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(1L, result.getId(), "Employee ID should match");
        assertEquals("Jane", result.getFirstName(), "First name should match");
        assertEquals("Smith", result.getLastName(), "Last name should match");

        // Verify Kafka event was published (even with null email/password/role)
        verify(kafkaProducerService, times(1)).publishUserCreatedEvent(any(UserEvent.class));
        verify(employeeRepository, times(1)).save(any(Employee.class));
    }

    /**
     * Test updateEmployee method when employee exists
     * Verifies that employee is updated with new properties
     */
    @Test
    void testUpdateEmployee_WhenEmployeeExists_ShouldUpdateAndReturnEmployee() {
        // Arrange
        Long employeeId = 1L;

        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setFirstName("Old First");
        existingEmployee.setLastName("Old Last");
        existingEmployee.setEmployeeId("OLD001");
        existingEmployee.setDepartmentId(1L);

        EmployeeRequest updateData = new EmployeeRequest();
        updateData.setFirstName("New First");
        updateData.setLastName("New Last");
        updateData.setEmployeeId("NEW001");

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(employeeId);
        updatedEmployee.setFirstName("New First");
        updatedEmployee.setLastName("New Last");
        updatedEmployee.setEmployeeId("NEW001");
        updatedEmployee.setDepartmentId(1L); // Unchanged

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenReturn(updatedEmployee);

        // Act
        Employee result = employeeService.updateEmployee(employeeId, updateData);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(employeeId, result.getId(), "Employee ID should match");
        assertEquals("New First", result.getFirstName(), "First name should be updated");
        assertEquals("New Last", result.getLastName(), "Last name should be updated");
        assertEquals("NEW001", result.getEmployeeId(), "Employee ID should be updated");
        assertEquals(1L, result.getDepartmentId(), "Department ID should remain unchanged");

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).save(existingEmployee);
    }

    /**
     * Test updateEmployee method when employee does not exist
     * Verifies that RuntimeException is thrown
     */
    @Test
    void testUpdateEmployee_WhenEmployeeDoesNotExist_ShouldThrowException() {
        // Arrange
        Long employeeId = 999L;
        EmployeeRequest updateData = new EmployeeRequest();
        updateData.setFirstName("Non-existent Employee");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> employeeService.updateEmployee(employeeId, updateData));

        assertEquals("Department not found", exception.getMessage());
        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, never()).save(any(Employee.class));
    }

    /**
     * Test updateEmployee with partial update (null properties should be ignored)
     * Verifies that only non-null properties are updated
     */
    @Test
    void testUpdateEmployee_WithPartialUpdate_ShouldUpdateOnlyNonNullProperties() {
        // Arrange
        Long employeeId = 1L;

        Employee existingEmployee = new Employee();
        existingEmployee.setId(employeeId);
        existingEmployee.setFirstName("Original First");
        existingEmployee.setLastName("Original Last");
        existingEmployee.setEmployeeId("ORIG001");
        existingEmployee.setDepartmentId(1L);

        EmployeeRequest updateData = new EmployeeRequest();
        updateData.setFirstName("Updated First");
        // Last name and employee ID are null, so they should remain unchanged

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(employeeId);
        updatedEmployee.setFirstName("Updated First");
        updatedEmployee.setLastName("Original Last"); // Unchanged
        updatedEmployee.setEmployeeId("ORIG001"); // Unchanged
        updatedEmployee.setDepartmentId(1L); // Unchanged

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(existingEmployee));
        when(employeeRepository.save(existingEmployee)).thenReturn(updatedEmployee);

        // Act
        Employee result = employeeService.updateEmployee(employeeId, updateData);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals("Updated First", result.getFirstName(), "First name should be updated");
        assertEquals("Original Last", result.getLastName(), "Last name should remain unchanged");
        assertEquals("ORIG001", result.getEmployeeId(), "Employee ID should remain unchanged");

        verify(employeeRepository, times(1)).findById(employeeId);
        verify(employeeRepository, times(1)).save(existingEmployee);
    }

    /**
     * Test deleteEmployee method when employee exists
     * Verifies that employee is deleted successfully
     */
    @Test
    void testDeleteEmployee_WhenEmployeeExists_ShouldDeleteEmployee() {
        // Arrange
        Long employeeId = 1L;

        when(employeeRepository.existsById(employeeId)).thenReturn(true);
        doNothing().when(employeeRepository).deleteById(employeeId);

        // Act
        employeeService.deleteEmployee(employeeId);

        // Assert
        verify(employeeRepository, times(1)).existsById(employeeId);
        verify(employeeRepository, times(1)).deleteById(employeeId);
    }

    /**
     * Test deleteEmployee method when employee does not exist
     * Verifies that RuntimeException is thrown
     */
    @Test
    void testDeleteEmployee_WhenEmployeeDoesNotExist_ShouldThrowException() {
        // Arrange
        Long employeeId = 999L;

        when(employeeRepository.existsById(employeeId)).thenReturn(false);

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> employeeService.deleteEmployee(employeeId));

        assertEquals("Department does not exist", exception.getMessage());
        verify(employeeRepository, times(1)).existsById(employeeId);
        verify(employeeRepository, never()).deleteById(any(Long.class));
    }

    /**
     * Test findEmployeeById method when employee exists
     * Verifies that employee is returned
     */
    @Test
    void testFindEmployeeById_WhenEmployeeExists_ShouldReturnEmployee() {
        // Arrange
        Long employeeId = 1L;
        Employee expectedEmployee = new Employee();
        expectedEmployee.setId(employeeId);
        expectedEmployee.setFirstName("John");
        expectedEmployee.setLastName("Doe");
        expectedEmployee.setEmployeeId("EMP001");

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(expectedEmployee));

        // Act
        Employee result = employeeService.findEmployeeById(employeeId);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(employeeId, result.getId(), "Employee ID should match");
        assertEquals("John", result.getFirstName(), "First name should match");
        assertEquals("Doe", result.getLastName(), "Last name should match");

        verify(employeeRepository, times(1)).findById(employeeId);
    }

    /**
     * Test findEmployeeById method when employee does not exist
     * Verifies that RuntimeException is thrown
     */
    @Test
    void testFindEmployeeById_WhenEmployeeDoesNotExist_ShouldThrowException() {
        // Arrange
        Long employeeId = 999L;

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> employeeService.findEmployeeById(employeeId));

        assertEquals("Department not found", exception.getMessage());
        verify(employeeRepository, times(1)).findById(employeeId);
    }

    /**
     * Test getAllEmployees method when employees exist
     * Verifies that list of employees is returned
     */
    @Test
    void testGetAllEmployees_WhenEmployeesExist_ShouldReturnEmployeeList() {
        // Arrange
        Employee emp1 = new Employee();
        emp1.setId(1L);
        emp1.setFirstName("John");
        emp1.setLastName("Doe");

        Employee emp2 = new Employee();
        emp2.setId(2L);
        emp2.setFirstName("Jane");
        emp2.setLastName("Smith");

        List<Employee> expectedEmployees = Arrays.asList(emp1, emp2);

        when(employeeRepository.findAll()).thenReturn(expectedEmployees);

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return 2 employees");
        assertEquals("John", result.get(0).getFirstName(), "First employee first name should match");
        assertEquals("Jane", result.get(1).getFirstName(), "Second employee first name should match");

        verify(employeeRepository, times(1)).findAll();
    }

    /**
     * Test getAllEmployees method when no employees exist
     * Verifies that empty list is returned
     */
    @Test
    void testGetAllEmployees_WhenNoEmployeesExist_ShouldReturnEmptyList() {
        // Arrange
        List<Employee> expectedEmployees = List.of();

        when(employeeRepository.findAll()).thenReturn(expectedEmployees);

        // Act
        List<Employee> result = employeeService.getAllEmployees();

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty list");

        verify(employeeRepository, times(1)).findAll();
    }

    /**
     * Test getDepartmentEmployees method when employees exist for department
     * Verifies that list of department employees is returned
     */
    @Test
    void testGetDepartmentEmployees_WhenEmployeesExist_ShouldReturnEmployeeList() {
        // Arrange
        Long departmentId = 1L;

        Employee emp1 = new Employee();
        emp1.setId(1L);
        emp1.setFirstName("John");
        emp1.setLastName("Doe");
        emp1.setDepartmentId(departmentId);

        Employee emp2 = new Employee();
        emp2.setId(2L);
        emp2.setFirstName("Jane");
        emp2.setLastName("Smith");
        emp2.setDepartmentId(departmentId);

        List<Employee> expectedEmployees = Arrays.asList(emp1, emp2);

        when(employeeRepository.findByDepartmentId(departmentId)).thenReturn(expectedEmployees);

        // Act
        List<Employee> result = employeeService.getDepartmentEmployees(departmentId);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertEquals(2, result.size(), "Should return 2 employees for department");
        assertTrue(result.stream().allMatch(emp -> departmentId.equals(emp.getDepartmentId())),
                "All employees should belong to the specified department");

        verify(employeeRepository, times(1)).findByDepartmentId(departmentId);
    }

    /**
     * Test getDepartmentEmployees method when no employees exist for department
     * Verifies that empty list is returned
     */
    @Test
    void testGetDepartmentEmployees_WhenNoEmployeesExist_ShouldReturnEmptyList() {
        // Arrange
        Long departmentId = 999L;
        List<Employee> expectedEmployees = List.of();

        when(employeeRepository.findByDepartmentId(departmentId)).thenReturn(expectedEmployees);

        // Act
        List<Employee> result = employeeService.getDepartmentEmployees(departmentId);

        // Assert
        assertNotNull(result, "Result should not be null");
        assertTrue(result.isEmpty(), "Should return empty list for department with no employees");

        verify(employeeRepository, times(1)).findByDepartmentId(departmentId);
    }
}