package ng.darum.employee.service;

import lombok.extern.slf4j.Slf4j;
import ng.darum.commons.dto.UserCreatedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class KafkaProducerService {
    private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

    @Value("${employee.topic.name}")
    private String userCreatedTopic;

    public KafkaProducerService(KafkaTemplate<String, UserCreatedEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void publishUserCreatedEvent(UserCreatedEvent event) {
        log.info("Publishing event to Kafka: {}", event);
        kafkaTemplate.send(userCreatedTopic, String.valueOf(event.getEmail()), event);
    }
}
