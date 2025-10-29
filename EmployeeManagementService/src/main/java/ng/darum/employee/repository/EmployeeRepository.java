package ng.darum.employee.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import ng.darum.employee.entity.Employee;

public interface EmployeeRepository extends JpaRepository<Employee,Long> {


    List<Employee> findByDepartmentId(Long departmentId);
}
