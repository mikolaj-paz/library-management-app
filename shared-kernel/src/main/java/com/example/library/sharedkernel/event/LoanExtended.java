package com.example.library.sharedkernel.event;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDate;

public class LoanExtended extends DomainEvent {

  private final LoanId loanId;
  private final ReaderId readerId;
  private final BookCopyId bookCopyId;
  private final LocalDate newDueDate;

  public LoanExtended(
      LoanId loanId, ReaderId readerId, BookCopyId bookCopyId, LocalDate newDueDate) {
    super();
    this.loanId = loanId;
    this.readerId = readerId;
    this.bookCopyId = bookCopyId;
    this.newDueDate = newDueDate;
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

  public LocalDate newDueDate() {
    return newDueDate;
  }
}
