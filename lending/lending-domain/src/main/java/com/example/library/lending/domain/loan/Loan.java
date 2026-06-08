package com.example.library.lending.domain.loan;

import com.example.library.lending.domain.exception.ExtensionNotAllowedException;
import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDate;

public class Loan extends AggregateRoot<LoanId> {

  private static final int LOAN_DURATION_DAYS = 14;
  private static final int EXTENSION_DURATION_DAYS = 14;

  private final BookCopyId bookCopyId;
  private final ReaderId readerId;
  private LocalDate dueDate;
  private LoanStatus status;

  private Loan(
      LoanId id, BookCopyId bookCopyId, ReaderId readerId, LocalDate dueDate, LoanStatus status) {
    super(id);
    this.bookCopyId = bookCopyId;
    this.readerId = readerId;
    this.dueDate = dueDate;
    this.status = status;
  }

  private boolean isClosed() {
    return status == LoanStatus.CLOSED;
  }

  private boolean isExtended() {
    return status == LoanStatus.EXTENDED;
  }

  public static Loan create(ReaderId readerId, BookCopyId bookCopyId) {
    return new Loan(
        LoanId.create(),
        bookCopyId,
        readerId,
        LocalDate.now().plusDays(LOAN_DURATION_DAYS),
        LoanStatus.ACTIVE);
  }

  public static Loan create(
      LoanId id, ReaderId readerId, BookCopyId bookCopyId, LocalDate dueDate, LoanStatus status) {
    return new Loan(id, bookCopyId, readerId, dueDate, status);
  }

  public BookCopyId bookCopyId() {
    return bookCopyId;
  }

  public ReaderId readerId() {
    return readerId;
  }

  public LocalDate dueDate() {
    return dueDate;
  }

  public LoanStatus status() {
    return status;
  }

  public boolean isOverdue(LocalDate on) {
    return on.isAfter(dueDate);
  }

  public void close() {
    this.status = LoanStatus.CLOSED;
  }

  public void extend() {
    if (this.isClosed()) {
      throw new ExtensionNotAllowedException("Loan is already closed.");
    }

    if (this.isExtended()) {
      throw new ExtensionNotAllowedException("Loan has already been extended once.");
    }

    this.dueDate = dueDate.plusDays(EXTENSION_DURATION_DAYS);
    this.status = LoanStatus.EXTENDED;
  }
}
