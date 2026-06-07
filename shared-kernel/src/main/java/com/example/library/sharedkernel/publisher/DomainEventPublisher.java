package com.example.library.sharedkernel.publisher;

import com.example.library.sharedkernel.event.DomainEvent;

public interface DomainEventPublisher {

  void publish(DomainEvent domainEvent);
}
