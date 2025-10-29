package ng.darum.employee.repository;

import ng.darum.employee.entity.Department;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class DepartmentRepositoryTest {

    @Autowired
    private DepartmentRepository departmentRepository;

    @Test
    void testExistsByName_ShouldReturnTrue_WhenDepartmentExists() {
        // Arrange
        Department department = new Department();
        department.setName("Engineering");
        departmentRepository.save(department);

        // Act
        boolean exists = departmentRepository.existsByName("Engineering");

        // Assert
        assertThat(exists).isTrue();
    }

    @Test
    void testExistsByName_ShouldReturnFalse_WhenDepartmentDoesNotExist() {
        // Act
        boolean exists = departmentRepository.existsByName("Marketing");

        // Assert
        assertThat(exists).isFalse();
    }
}
