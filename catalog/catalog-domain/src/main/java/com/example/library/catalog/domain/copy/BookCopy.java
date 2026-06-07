package com.example.library.catalog.domain.copy;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.primitives.AggregateRoot;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;

public class BookCopy extends AggregateRoot<BookCopyId> {

  private BookCopyStatus status;

  private BookCopy(BookCopyId id, BookCopyStatus status) {
    super(id);
    this.status = status;
  }

  public static BookCopy create(BookCopyId id, BookCopyStatus status) {
    return new BookCopy(id, status);
  }

  public boolean isAvailable() {
    return status == BookCopyStatus.AVAILABLE;
  }
}
