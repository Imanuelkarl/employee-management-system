package ng.darum.auth.services;

import lombok.extern.slf4j.Slf4j;
import ng.darum.auth.dto.UserRequest;
import ng.darum.commons.dto.UserEvent;
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
    public void consumeCreate(UserEvent event) {
        extracted(event);

        UserRequest user = new UserRequest();
        user.setEmpId(event.getId());
        user.setEmail(event.getEmail());
        user.setPassword(event.getPassword());
        user.setRole(event.getRole());
        authenticationService.createUser(user);
        log.info("Employee record created for userId {}", event.getEmail());
    }
    @KafkaListener(topics = "${delete.topic.name}", groupId = "employee-group")
    public void consumeDelete(UserEvent event) {
        extracted(event);
        authenticationService.deleteUser(event.getId());
        log.info("User record deleted for user {}", event.getId());
    }
    @KafkaListener(topics = "${update.topic.name}", groupId = "employee-group")
    public void consumeUpdate(UserEvent event) {
        extracted(event);

        UserRequest user = new UserRequest();
        user.setEmpId(event.getId());
        user.setEmail(event.getEmail());
        user.setPassword(event.getPassword());
        user.setRole(event.getRole());
        authenticationService.updateUser(event.getId(), user);
    }

    private static void extracted(UserEvent event) {
        log.info("Received event: {}", event);
    }
}

