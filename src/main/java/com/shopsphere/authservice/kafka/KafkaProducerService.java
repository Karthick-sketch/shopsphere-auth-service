package com.shopsphere.authservice.kafka;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class KafkaProducerService {

  private final String topic;
  private final KafkaTemplate<String, UserCreatedEvent> kafkaTemplate;

  public KafkaProducerService(
    @Value("${kafka.topic.user-created}") String topic,
    KafkaTemplate<String, UserCreatedEvent> kafkaTemplate
  ) {
    this.topic = topic;
    this.kafkaTemplate = kafkaTemplate;
  }

  public void sendUserCreatedEvent(UserCreatedEvent event) {
    kafkaTemplate.send(topic, event);
  }
}
