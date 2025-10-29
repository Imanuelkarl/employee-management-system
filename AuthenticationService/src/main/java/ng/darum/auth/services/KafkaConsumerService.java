package ng.darum.employee.service;

import lombok.extern.slf4j.Slf4j;
import ng.darum.auth.dto.UserCreatedEvent;
import ng.darum.employee.entity.Employee;
import ng.darum.employee.repository.EmployeeRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    private final EmployeeRepository employeeRepository;

    public KafkaConsumerService(EmployeeRepository employeeRepository) {
        this.employeeRepository = employeeRepository;
    }

    @KafkaListener(topics = "${employee.topic.name}", groupId = "employee-group")
    public void consume(UserCreatedEvent event) {
        log.info("Received event: {}", event);

        // Only create employee if role is EMPLOYEE
        Employee employee = new Employee();
        employee.setUserId(event.getUserId());
        employee.setFirstName(event.getFirstName());
        employee.setLastName(event.getLastName());
        employee.setDepartment(event.getDepartment());
        employee.setEmployeeId(event.getEmployeeId());
        employee.setStatus(event.getStatus());

        employeeRepository.save(employee);
        log.info("Employee record created for userId {}", event.getUserId());
    }
}

