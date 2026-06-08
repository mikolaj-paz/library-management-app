package com.example.library.lending.domain.loan;

import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDate;

public class BookCopyLoaned extends DomainEvent {

  private final LoanId loanId;
  private final ReaderId readerId;
  private final BookCopyId bookCopyId;
  private final LocalDate loanDueDate;

  public BookCopyLoaned(
      LoanId loanId, ReaderId readerId, BookCopyId bookCopyId, LocalDate loanDueDate) {
    super();
    this.loanId = loanId;
    this.readerId = readerId;
    this.bookCopyId = bookCopyId;
    this.loanDueDate = loanDueDate;
  }

  @Override
  public String name() {
    return "BookCopyLoaned";
  }

  public LoanId loanId() {
    return loanId;
  }

  public ReaderId readerId() {
    return readerId;
  }

  public BookCopyId bookCopyId() {
    return bookCopyId;
  }

  public LocalDate loanDueDate() {
    return loanDueDate;
  }
}
