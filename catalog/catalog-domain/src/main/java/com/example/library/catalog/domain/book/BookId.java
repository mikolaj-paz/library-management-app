package com.example.library.catalog.domain.book;

import java.util.UUID;

public record BookId(UUID value) {
  public static BookId of(String id) {
    return new BookId(UUID.fromString(id));
  }
}
