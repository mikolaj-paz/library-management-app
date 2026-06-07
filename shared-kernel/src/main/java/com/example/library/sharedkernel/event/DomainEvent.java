package com.example.library.sharedkernel.event;

import java.time.Instant;

public interface DomainEvent {
  Instant occurredOn();
}
