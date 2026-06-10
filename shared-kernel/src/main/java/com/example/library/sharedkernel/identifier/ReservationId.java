package com.example.library.sharedkernel.identifier;

import java.util.Objects;
import java.util.UUID;

public record ReservationId(UUID value) {

  public ReservationId {
    Objects.requireNonNull(value, "Reservation ID must not be null");
  }

  public static ReservationId create() {
    return new ReservationId(UUID.randomUUID());
  }

  public static ReservationId of(String id) {
    return new ReservationId(UUID.fromString(id));
  }
}
