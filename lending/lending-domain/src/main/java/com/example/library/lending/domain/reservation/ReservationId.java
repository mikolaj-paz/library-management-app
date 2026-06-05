package com.example.library.lending.domain.reservation;

import java.util.Objects;
import java.util.UUID;

public record ReservationId(UUID id) {

  public ReservationId {
    Objects.requireNonNull(id, "Reservation ID must not be null");
  }
}
