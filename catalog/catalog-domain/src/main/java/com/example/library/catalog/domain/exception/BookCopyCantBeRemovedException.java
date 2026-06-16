package com.example.library.catalog.domain.exception;

import com.example.library.sharedkernel.identifier.BookCopyId;

public class BookCopyCantBeRemovedException extends RuntimeException {
  public BookCopyCantBeRemovedException(BookCopyId bookCopyId) {
    super(
        "Book copy "
            + bookCopyId
            + " can't be removed, because it is currently loaned or reserved.");
  }
}
