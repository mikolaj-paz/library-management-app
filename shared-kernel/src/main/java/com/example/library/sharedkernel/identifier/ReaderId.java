package com.example.library.sharedkernel.identifier;

import java.util.Objects;
import java.util.UUID;

public record ReaderId(UUID value) {

  public ReaderId {
    Objects.requireNonNull(value, "Reader ID must not be null");
  }

  public static ReaderId create() {
    return new ReaderId(UUID.randomUUID());
  }

  public static ReaderId of(String id) {
    return new ReaderId(UUID.fromString(id));
  }
}
