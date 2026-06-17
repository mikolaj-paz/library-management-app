package com.example.library.lending.domain.exception;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;

public class BookAlreadyInReaderWaitingQueueException extends RuntimeException {
  public BookAlreadyInReaderWaitingQueueException(ReaderId readerId, BookId bookId) {
    super(
        "Reader with ID "
            + readerId.value().toString()
            + " already has an active entry in the waiting queue for book with ID "
            + bookId.value().toString());
  }
}
