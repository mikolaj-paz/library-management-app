package com.example.library.sharedkernel.event;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;

public class BookCopyReturned extends DomainEvent {

  private final ReaderId readerId;
  private final BookCopyId bookCopyId;
  private final boolean isOverdue;

  public BookCopyReturned(ReaderId readerId, BookCopyId bookCopyId, boolean isOverdue) {
    super();
    this.readerId = readerId;
    this.bookCopyId = bookCopyId;
    this.isOverdue = isOverdue;
  }

  public ReaderId readerId() {
    return readerId;
  }

  public BookCopyId bookCopyId() {
    return bookCopyId;
  }

  public boolean isOverdue() {
    return isOverdue;
  }
}
