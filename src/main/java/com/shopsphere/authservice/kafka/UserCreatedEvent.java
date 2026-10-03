package com.shopsphere.authservice.kafka;

import com.shopsphere.authservice.dto.UserCreatedData;
import java.time.Instant;
import java.util.UUID;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserCreatedEvent {

  private UUID eventId;
  private String eventType;
  private Instant occurredAt;
  private UserCreatedData data;

  public UserCreatedEvent(Long userId, String name, String email) {
    this.data = new UserCreatedData(userId, name, email);
    this.eventId = UUID.randomUUID();
    this.eventType = KafkaConstants.USER_CREATED_EVENT_TYPE;
    this.occurredAt = Instant.now();
  }
}
