package com.example.library.sharedkernel.identifier;

import java.util.Objects;
import java.util.UUID;

public record PatronId(UUID id) {

  public PatronId {
    Objects.requireNonNull(id, "Patron ID must not be null");
  }
}
