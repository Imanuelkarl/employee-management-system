package ng.darum.auth.feign;

import ng.darum.auth.dto.Employee;
import ng.darum.auth.dto.EmployeeRequest;
import ng.darum.auth.dto.ServerResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@FeignClient(name = "employee-service", url = "localhost:8081")
public interface EmployeeInterface {

    @PostMapping("/employee/create")  // Added explicit path
    ResponseEntity<ServerResponse<Employee>> createEmployee(@RequestBody EmployeeRequest request);

    @PutMapping("/employee/{id}")
    ResponseEntity<Employee> updateDepartment(@PathVariable Long id, @RequestBody Employee employee);

    @DeleteMapping("/employee/{id}")
    ResponseEntity<?> deleteEmployee(@PathVariable Long id);

    @GetMapping("/employee/{id}")
    ResponseEntity<Employee> findEmployee(@PathVariable Long id);

    @GetMapping("/employee")
    ResponseEntity<List<Employee>> getAllEmployee();
}