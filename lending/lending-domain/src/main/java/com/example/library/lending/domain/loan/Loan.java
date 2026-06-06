package com.example.library.lending.domain.loan;

import com.example.library.lending.domain.event.LoanedBookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.primitives.AggregateRoot;
import java.time.Instant;
import java.time.LocalDate;

public class Loan extends AggregateRoot<LoanId> {

  private final BookCopyId bookCopyId;
  private final ReaderId readerId;
  private final LocalDate dueDate;

  private static final int LOAN_DURATION_DAYS = 14;

  private Loan(LoanId id, BookCopyId bookCopyId, ReaderId readerId, LocalDate dueDate) {
    super(id);
    this.bookCopyId = bookCopyId;
    this.readerId = readerId;
    this.dueDate = dueDate;
    registerEvent(new LoanedBookCopy(this.id(), bookCopyId, readerId, dueDate, Instant.now()));
  }

  public static Loan create(ReaderId readerId, BookCopyId bookCopyId) {
    return new Loan(
        LoanId.create(), bookCopyId, readerId, LocalDate.now().plusDays(LOAN_DURATION_DAYS));
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
}
