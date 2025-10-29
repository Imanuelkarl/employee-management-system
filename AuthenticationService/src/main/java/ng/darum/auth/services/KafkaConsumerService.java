package ng.darum.auth.services;

import lombok.extern.slf4j.Slf4j;
import ng.darum.auth.dto.UserRequest;
import ng.darum.commons.dto.UserCreatedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaConsumerService {

    private final AuthenticationService authenticationService;

    public KafkaConsumerService(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @KafkaListener(topics = "${employee.topic.name}", groupId = "employee-group")
    public void consume(UserCreatedEvent event) {
        log.info("Received event: {}", event);

        // Only create employee if role is EMPLOYEE
        UserRequest user = new UserRequest();
        user.setEmail(event.getEmail());
        user.setPassword(event.getPassword());
        user.setRole(event.getRole());
        authenticationService.createUser(user);
        log.info("Employee record created for userId {}", event.getEmail());
    }
}

