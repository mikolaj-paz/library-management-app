package com.example.library.domain.lending.copy;

import java.util.Objects;
import java.util.UUID;

public record BookCopyId(UUID id) {

  public BookCopyId {
    Objects.requireNonNull(id, "Copy ID must not be null");
  }
}
