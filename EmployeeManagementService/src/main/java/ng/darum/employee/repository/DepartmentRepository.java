package ng.darum.employee.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import ng.darum.employee.entity.Department;

import java.util.Optional;

public interface DepartmentRepository extends JpaRepository<Department, Long> {
    boolean existsByName(String name);
    Optional<Department> findByName(String name);
}
