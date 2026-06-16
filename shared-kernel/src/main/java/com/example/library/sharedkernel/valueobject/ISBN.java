package com.example.library.sharedkernel.valueobject;

public record ISBN(String value) {
  public ISBN {
    if (value == null || value.isBlank()) {
      throw new IllegalArgumentException("ISBN cannot be null or blank");
    }
  }
}
