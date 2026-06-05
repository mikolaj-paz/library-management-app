package com.example.library.domain.lending.factories;

import com.example.library.domain.lending.copy.BookCopy;
import com.example.library.domain.lending.copy.BookCopyId;
import com.example.library.domain.lending.copy.BookCopyStatus;

public class BookCopyFactory {

  public BookCopy create(BookCopyId id) {
    // This should also register the event in the domain event system
    return new BookCopy(id, BookCopyStatus.AVAILABLE);
  }
}
