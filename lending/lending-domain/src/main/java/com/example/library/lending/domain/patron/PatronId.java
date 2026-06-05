package com.example.library.lending.domain.patron;

import java.util.Objects;
import java.util.UUID;

public record PatronId(UUID id) {

  public PatronId {
    Objects.requireNonNull(id, "Patron ID must not be null");
  }
}
