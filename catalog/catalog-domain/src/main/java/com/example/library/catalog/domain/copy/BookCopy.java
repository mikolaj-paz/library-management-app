package com.example.library.catalog.domain.copy;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.primitives.AggregateRoot;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;

public class BookCopy extends AggregateRoot<BookCopyId> {

  private BookCopyStatus status;
  private ReaderId reservedBy;
  private final BookId bookId;

  private BookCopy(BookCopyId id, BookId bookId) {
    super(id);
    this.status = BookCopyStatus.AVAILABLE;
    this.reservedBy = null;
    this.bookId = bookId;
  }

  public static BookCopy create(BookId bookId) {
    return new BookCopy(BookCopyId.create(), bookId);
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
}
