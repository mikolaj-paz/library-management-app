package com.example.library.catalog.domain.copy;

import com.example.library.catalog.domain.exception.BookCopyCantBeRemovedException;
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
  }

  private boolean isLoaned() {
    return status == BookCopyStatus.LOANED;
  }

  private boolean isReserved() {
    return status == BookCopyStatus.RESERVED;
  }

  static BookCopy create(BookCopyId id, BookCopyStatus status, ReaderId reservedBy, BookId bookId) {
    var bookCopy = new BookCopy(id, bookId, status, reservedBy);
    bookCopy.registerEvent(new BookCopyAdded(bookCopy.id(), bookCopy.bookId()));
    return bookCopy;
  }

  static BookCopy reconstitute(
      BookCopyId id, BookCopyStatus status, ReaderId reservedBy, BookId bookId) {
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
    if (this.isLoaned() || this.isReserved()) {
      throw new BookCopyCantBeRemovedException(this.id());
    }
    this.status = BookCopyStatus.WITHDRAWN;
    this.registerEvent(new BookCopyRemoved(this.id()));
  }
}
