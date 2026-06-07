package com.example.library.catalog.infrastructure.out;

import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;

public class DomainEventPublisherImpl implements DomainEventPublisher {

  private final ApplicationEventPublisher springEventPublisher;

  public DomainEventPublisherImpl(ApplicationEventPublisher springEventPublisher) {
    this.springEventPublisher = springEventPublisher;
  }

  @Override
  public void publish(DomainEvent event) {
    springEventPublisher.publishEvent(event);
  }
}
