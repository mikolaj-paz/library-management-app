package com.example.library.sharedkernel.identifier;

import java.util.Objects;
import java.util.UUID;

public record ReaderAccountId(UUID value) {

  public ReaderAccountId {
    Objects.requireNonNull(value, "Reader Account ID must not be null");
  }

  public static ReaderAccountId create() {
    return new ReaderAccountId(UUID.randomUUID());
  }

  public static ReaderAccountId of(String id) {
    return new ReaderAccountId(UUID.fromString(id));
  }
}
