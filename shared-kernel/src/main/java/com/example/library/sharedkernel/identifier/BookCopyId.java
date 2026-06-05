package com.example.library.sharedkernel.identifier;

import java.util.Objects;
import java.util.UUID;

public record BookCopyId(UUID id) {

  public BookCopyId {
    Objects.requireNonNull(id, "Copy ID must not be null");
  }
}
