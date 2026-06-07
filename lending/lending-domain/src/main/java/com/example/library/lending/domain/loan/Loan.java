package com.example.library.lending.domain.loan;

import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
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
