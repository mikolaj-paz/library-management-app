package com.example.library.sharedkernel.event;

import com.example.library.sharedkernel.entity.Entity;
import java.time.Instant;

public abstract class DomainEvent extends Entity<EventId> {

  private final Instant occuredOn;

  protected DomainEvent() {
    super(EventId.create());
    this.occuredOn = Instant.now();
  }

  public Instant occuredOn() {
    return occuredOn;
  }

  public abstract String eventName();
}
