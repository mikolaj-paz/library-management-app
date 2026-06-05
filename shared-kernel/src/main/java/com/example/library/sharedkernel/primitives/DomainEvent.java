package com.example.library.sharedkernel.primitives;

import java.time.Instant;

public interface DomainEvent {
  Instant occurredOn();
}
