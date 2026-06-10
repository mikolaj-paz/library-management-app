package com.example.library.catalog.domain.copy;

import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.identifier.BookCopyId;

public class BookCopyRemoved extends DomainEvent {

  private final BookCopyId bookCopyId;

  public BookCopyRemoved(BookCopyId bookCopyId) {
    super();
    this.bookCopyId = bookCopyId;
  }

  public BookCopyId bookCopyId() {
    return bookCopyId;
  }
}
