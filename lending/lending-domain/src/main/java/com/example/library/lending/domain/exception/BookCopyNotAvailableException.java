package com.example.library.lending.domain.exception;

import com.example.library.sharedkernel.identifier.BookCopyId;

public class BookCopyNotAvailableException extends RuntimeException {
  public BookCopyNotAvailableException(BookCopyId bookCopyId) {
    super("Book copy " + bookCopyId.value() + " is not available for lending.");
  }
}
