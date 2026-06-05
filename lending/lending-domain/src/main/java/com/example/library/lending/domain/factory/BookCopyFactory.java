package com.example.library.lending.domain.factory;

import com.example.library.lending.domain.copy.BookCopy;
import com.example.library.lending.domain.copy.BookCopyStatus;
import com.example.library.sharedkernel.identifier.BookCopyId;

public class BookCopyFactory {

  public BookCopy create(BookCopyId id) {
    // This should also register the event in the domain event system
    return new BookCopy(id, BookCopyStatus.AVAILABLE);
  }
}
