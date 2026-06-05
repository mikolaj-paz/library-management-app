package com.example.library.lending.domain.copy;

import com.example.library.sharedkernel.primitives.AggregateRoot;

public class BookCopy extends AggregateRoot<BookCopyId> {

  private BookCopyStatus status;

  private void changeStatus(BookCopyStatus newStatus) {
    this.status = newStatus;
  }

  public BookCopy(BookCopyId id, BookCopyStatus status) {
    super(id);
    changeStatus(status);
  }
}
