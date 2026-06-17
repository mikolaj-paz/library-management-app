package com.example.library.sharedkernel.event;

import com.example.library.sharedkernel.identifier.BookCopyId;

public class BookCopyReservationExpired extends DomainEvent {

  private final BookCopyId bookCopyId;

  public BookCopyReservationExpired(BookCopyId bookCopyId) {
    super();
    this.bookCopyId = bookCopyId;
  }

  public BookCopyId bookCopyId() {
    return bookCopyId;
  }
}
