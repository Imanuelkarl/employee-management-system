package ng.darum.employee.service;

import lombok.extern.slf4j.Slf4j;
import ng.darum.commons.dto.UserEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, UserEvent> kafkaTemplate;

    @Value("${employee.topic.name}")
    private String userCreatedTopic;

    @Value("${update.topic.name}")
    private String userUpdatedTopic;

    @Value("${delete.topic.name}")
    private String userDeletedTopic;


    public KafkaProducerService(KafkaTemplate<String, UserEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserCreatedEvent(UserEvent event) {
        getInfo(event);
        kafkaTemplate.send(userCreatedTopic, String.valueOf(event.getEmail()), event);
    }
    public void publishUserUpdatedEvent(UserEvent event) {
        getInfo(event);
        kafkaTemplate.send(userUpdatedTopic, String.valueOf(event.getEmail()), event);
    }
    public void publishUserDeletedEvent(UserEvent event) {
        getInfo(event);
        kafkaTemplate.send(userDeletedTopic, String.valueOf(event.getEmail()), event);
    }

    private static void getInfo(UserEvent event) {
        log.info("Publishing event to Kafka: {}", event);
    }
}
