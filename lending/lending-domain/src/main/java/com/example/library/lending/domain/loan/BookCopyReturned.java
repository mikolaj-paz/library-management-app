package com.example.library.lending.domain.loan;

import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;

public class BookCopyReturned extends DomainEvent {

  private final LoanId loanId;
  private final ReaderId readerId;
  private final BookCopyId bookCopyId;
  private final boolean isOverdue;

  public BookCopyReturned(
      LoanId loanId, ReaderId readerId, BookCopyId bookCopyId, boolean isOverdue) {
    this.loanId = loanId;
    this.readerId = readerId;
    this.bookCopyId = bookCopyId;
    this.isOverdue = isOverdue;
  }

  @Override
  public String name() {
    return "BookCopyReturned";
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

  public boolean isOverdue() {
    return isOverdue;
  }
}
