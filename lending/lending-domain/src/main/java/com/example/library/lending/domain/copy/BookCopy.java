package com.example.library.lending.domain.copy;

import com.example.library.lending.domain.exception.BookCopyNotAvailableException;
import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.event.BookCopyLoaned;
import com.example.library.sharedkernel.event.BookCopyReserved;
import com.example.library.sharedkernel.event.BookCopyReturned;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.identifier.ReservationId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;

public class BookCopy extends AggregateRoot<BookCopyId> {

  private BookCopyStatus status;
  private ReaderId reservedBy;
  private final BookId bookId;

  private BookCopy(BookCopyId id, BookCopyStatus status, ReaderId reservedBy, BookId bookId) {
    super(id);
    this.status = status;
    this.reservedBy = reservedBy;
    this.bookId = bookId;
  }

  static BookCopy of(BookCopyId id, BookCopyStatus status, ReaderId reservedBy, BookId bookId) {
    return new BookCopy(id, status, reservedBy, bookId);
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

  public void verifyCanBeLoanedBy(ReaderId readerId) {
    if (status == BookCopyStatus.AVAILABLE) {
      return;
    }
    if (status == BookCopyStatus.RESERVED && readerId.equals(reservedBy)) {
      return;
    }
    throw new BookCopyNotAvailableException(this.id());
  }

  public void lend(ReaderId readerId, LoanId loanId) {
    this.status = BookCopyStatus.LOANED;
    this.reservedBy = null;
    this.registerEvent(new BookCopyLoaned(loanId, readerId, this.id()));
  }

  public void reserve(ReservationId reservationId, ReaderId readerId) {
    this.status = BookCopyStatus.RESERVED;
    this.reservedBy = readerId;
    this.registerEvent(new BookCopyReserved(reservationId, readerId, this.id()));
  }

  public void returnIt(ReaderId readerId, boolean isOverdue) {
    this.status = BookCopyStatus.AVAILABLE;
    this.registerEvent(new BookCopyReturned(readerId, this.id(), isOverdue));
  }
}
