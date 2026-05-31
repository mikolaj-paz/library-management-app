package com.example.library.shared;

import java.time.Instant;

public interface DomainEvent {
  Instant occurredOn();
}
