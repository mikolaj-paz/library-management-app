package com.example.library.sharedkernel.event;

import com.example.library.sharedkernel.entity.Entity;

public abstract class DomainEvent extends Entity<EventId> {

  protected DomainEvent() {
    super(EventId.create());
  }
}
