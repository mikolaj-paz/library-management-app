package com.example.library.catalog.application.query;

public record SearchCatalog(String phrase) {
  public SearchCatalog {
    if (phrase == null || phrase.isBlank()) {
      throw new IllegalArgumentException("Search phrase cannot be null or blank");
    }
  }
}
