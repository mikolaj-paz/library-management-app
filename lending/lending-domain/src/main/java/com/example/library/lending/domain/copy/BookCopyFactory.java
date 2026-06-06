package com.example.library.lending.domain.copy;

import com.example.library.sharedkernel.identifier.BookCopyId;

public class BookCopyFactory {

  public BookCopy addBookCopy(BookCopyId id) {
    return new BookCopy(id, BookCopyStatus.AVAILABLE);
  }
}
