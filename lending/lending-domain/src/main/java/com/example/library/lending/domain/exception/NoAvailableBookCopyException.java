package com.example.library.lending.domain.exception;

import com.example.library.sharedkernel.identifier.BookId;

public class NoAvailableBookCopyException extends RuntimeException {
  public NoAvailableBookCopyException(BookId bookId) {
    super(
        "No available copies for book with ID: "
            + bookId.value()
            + ". You can sign up for a queue for this book.");
  }
}
