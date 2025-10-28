package ng.darum.employee.service;

import ng.darum.employee.dto.EmployeeRequest;
import ng.darum.employee.dto.ServerResponse;
import ng.darum.employee.entity.Employee;
import ng.darum.employee.repository.EmployeeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class EmployeeServiceTests {

    @Mock
    private EmployeeRepository employeeRepository;

    @InjectMocks
    private EmployeeService employeeService;

    @Test
    public void EmployeeService_CreateEmployee_ReturnsEmployeeDto(){
        Employee employee = Employee.builder()
                .firstName("Emmanuel")
                .lastName("Dozie")
                .department("ICT")
                .userId(1L)
                .build();
        EmployeeRequest employeeRequest = EmployeeRequest.builder()
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .department(employee.getDepartment())
                .userId(employee.getUserId())
                .build();
        when(employeeRepository.save(Mockito.any(Employee.class))).thenReturn(employee);

        ServerResponse<Object> savedEmployee = employeeService.createEmployee(employeeRequest);

        Assertions.assertThat(savedEmployee).isNotNull();
    }
}
