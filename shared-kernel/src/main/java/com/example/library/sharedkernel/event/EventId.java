package com.example.library.sharedkernel.event;

import java.util.Objects;
import java.util.UUID;

public record EventId(UUID value) {

  public EventId {
    Objects.requireNonNull(value, "Event ID must not be null");
  }

  public static EventId create() {
    return new EventId(UUID.randomUUID());
  }
}
