package com.example.library.domain.model;

import java.util.Objects;
import java.util.UUID;

public record CopyId(UUID id) {

  public CopyId {
    Objects.requireNonNull(id, "Copy ID must not be null");
  }
}
