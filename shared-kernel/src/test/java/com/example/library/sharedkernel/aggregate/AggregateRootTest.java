package com.example.library.sharedkernel.aggregate;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.sharedkernel.event.DomainEvent;
import java.time.Instant;
import org.junit.jupiter.api.Test;

class AggregateRootTest {

  @Test
  void should_return_and_clear_domain_events_when_events_are_pulled() {
    var aggregate = new TestAggregate("aggregate-1");
    var event = new TestEvent(Instant.now());

    aggregate.raise(event);

    assertThat(aggregate.pullDomainEvents()).containsExactly(event);
    assertThat(aggregate.pullDomainEvents()).isEmpty();
  }

  private static final class TestAggregate extends AggregateRoot<String> {

    private TestAggregate(String id) {
      super(id);
    }

    private void raise(DomainEvent event) {
      registerEvent(event);
    }
  }

  private record TestEvent(Instant occurredOn) implements DomainEvent {}
}
