package com.example.library.sharedkernel.identifier;

import java.util.Objects;
import java.util.UUID;

public record BookCopyId(UUID value) {

  public BookCopyId {
    Objects.requireNonNull(value, "Copy ID must not be null");
  }

  public static BookCopyId create() {
    return new BookCopyId(UUID.randomUUID());
  }

  public static BookCopyId of(String id) {
    return new BookCopyId(UUID.fromString(id));
  }
}
