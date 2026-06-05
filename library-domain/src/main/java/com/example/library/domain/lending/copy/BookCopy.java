package com.example.library.domain.lending.copy;

import com.example.library.shared.AggregateRoot;

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
