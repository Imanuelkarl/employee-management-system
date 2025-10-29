package ng.darum.employee.repository;

import ng.darum.employee.entity.Employee;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    @Test
    void testFindByDepartmentId_ShouldReturnEmployeesInSameDepartment() {
        // Arrange
        Employee emp1 = Employee.builder()
                .firstName("Alice")
                .lastName("Smith")
                .employeeId("EMP001")
                .departmentId(100L)
                .status("ACTIVE")
                .build();

        Employee emp2 = Employee.builder()
                .firstName("Bob")
                .lastName("Johnson")
                .employeeId("EMP002")
                .departmentId(100L)
                .status("ACTIVE")
                .build();

        Employee emp3 = Employee.builder()
                .firstName("Charlie")
                .lastName("Brown")
                .employeeId("EMP003")
                .departmentId(200L)
                .status("ACTIVE")
                .build();

        employeeRepository.saveAll(List.of(emp1, emp2, emp3));

        // Act
        List<Employee> departmentEmployees = employeeRepository.findByDepartmentId(100L);

        // Assert
        assertThat(departmentEmployees).hasSize(2);
        assertThat(departmentEmployees)
                .extracting(Employee::getEmployeeId)
                .containsExactlyInAnyOrder("EMP001", "EMP002");
    }
}
