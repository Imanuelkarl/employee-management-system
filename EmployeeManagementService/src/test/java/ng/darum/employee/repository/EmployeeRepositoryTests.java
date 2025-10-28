package ng.darum.employee.repository;

import ng.darum.employee.entity.Employee;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

@DataJpaTest
@AutoConfigureTestDatabase( connection = EmbeddedDatabaseConnection.H2)
public class EmployeeRepositoryTests {
    @Autowired
    EmployeeRepository employeeRepository;

    @Test
    public void EmployeeRepository_SaveAll_ReturnSavedEmployee(){
        //Arrange
        Employee employee = Employee.builder()
                .firstName("Emmanuel")
                .lastName("Dozie")
                .department("ICT")
                .userId(1L)
                .build();

        //Act
        Employee savedEmployee = employeeRepository.save(employee);

        //Assert
        Assertions.assertThat(savedEmployee).isNotNull();
        Assertions.assertThat(savedEmployee.getId()).isGreaterThan(0);
    }
}
