package com.example.library.lending.domain.copy;

import com.example.library.lending.domain.exception.BookCopyNotAvailableException;
import com.example.library.sharedkernel.aggregate.AggregateRoot;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;

public class BookCopy extends AggregateRoot<BookCopyId> {

  private BookCopyStatus status;
  private ReaderId reservedBy;

  private BookCopy(BookCopyId id, BookCopyStatus status, ReaderId reservedBy) {
    super(id);
    this.reservedBy = reservedBy;
    this.status = status;
  }

  public static BookCopy create(BookCopyId id, BookCopyStatus status, ReaderId reservedBy) {
    return new BookCopy(id, status, reservedBy);
  }

  public BookCopyStatus status() {
    return status;
  }

  public ReaderId reservedBy() {
    return reservedBy;
  }

  public void verifyCanBeLoanedBy(ReaderId readerId) {
    if (status == BookCopyStatus.AVAILABLE) {
      return;
    }
    if (status == BookCopyStatus.RESERVED && readerId.equals(reservedBy)) {
      return;
    }
    throw new BookCopyNotAvailableException(this.id());
  }

  public void updateStatusAsLoaned() {
    this.status = BookCopyStatus.LOANED;
    this.reservedBy = null;
  }

  public void updateStatusAsReserved(ReaderId readerId) {
    this.status = BookCopyStatus.RESERVED;
    this.reservedBy = readerId;
  }
}
