package com.example.library.catalog.domain.copy;

import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;

public class BookCopy extends AggregateRoot<BookCopyId> {

  private BookCopyStatus status;
  private ReaderId reservedBy;
  private final BookId bookId;

  private BookCopy(BookCopyId id, BookId bookId, BookCopyStatus status, ReaderId reservedBy) {
    super(id);
    this.status = status;
    this.reservedBy = reservedBy;
    this.bookId = bookId;
    this.registerEvent(new BookCopyAdded(this.id(), bookId));
  }

  static BookCopy of(BookCopyId id, BookCopyStatus status, ReaderId reservedBy, BookId bookId) {
    return new BookCopy(id, bookId, status, reservedBy);
  }

  public BookCopyStatus status() {
    return status;
  }

  public ReaderId reservedBy() {
    return reservedBy;
  }

  public BookId bookId() {
    return bookId;
  }

  public boolean isAvailable() {
    return status == BookCopyStatus.AVAILABLE;
  }

  public void remove() {
    this.status = BookCopyStatus.UNAVAILABLE;
    this.registerEvent(new BookCopyRemoved(this.id()));
  }
}
