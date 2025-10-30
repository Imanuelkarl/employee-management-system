package ng.darum.employee.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Example;
import org.springframework.data.jpa.repository.JpaRepository;

import ng.darum.employee.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {

    boolean existsByEmail(String email);

    List<Employee> findByDepartmentId(Long departmentId);

    Optional<Employee> findByEmail(String userEmail);
}
