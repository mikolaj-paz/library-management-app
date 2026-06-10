package com.example.library.catalog.domain.copy;

import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;

public class BookCopyAdded extends DomainEvent {

  private final BookCopyId bookCopyId;
  private final BookId bookId;

  public BookCopyAdded(BookCopyId bookCopyId, BookId bookId) {
    super();
    this.bookCopyId = bookCopyId;
    this.bookId = bookId;
  }

  public BookCopyId bookCopyId() {
    return bookCopyId;
  }

  public BookId bookId() {
    return bookId;
  }
}
