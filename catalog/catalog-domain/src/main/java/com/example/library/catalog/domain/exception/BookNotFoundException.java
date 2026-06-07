package com.example.library.catalog.domain.exception;

import com.example.library.sharedkernel.identifier.BookId;

public class BookNotFoundException extends RuntimeException {
  public BookNotFoundException(BookId bookId) {
    super("Book with ID " + bookId.value() + " not found.");
  }
}
