package com.example.library.sharedkernel.event;

import com.example.library.sharedkernel.entity.Entity;
import java.time.LocalDateTime;

public abstract class DomainEvent extends Entity<EventId> {

  private final LocalDateTime occurredOn;

  protected DomainEvent() {
    super(EventId.create());
    this.occurredOn = LocalDateTime.now();
  }

  public LocalDateTime occurredOn() {
    return occurredOn;
  }
}
