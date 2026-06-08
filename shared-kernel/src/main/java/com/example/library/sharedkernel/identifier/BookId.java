package com.example.library.sharedkernel.identifier;

import java.util.Objects;
import java.util.UUID;

public record BookId(UUID value) {

  public BookId {
    Objects.requireNonNull(value, "Book ID must not be null");
  }

  public static BookId create() {
    return new BookId(UUID.randomUUID());
  }

  public static BookId of(String id) {
    return new BookId(UUID.fromString(id));
  }
}
