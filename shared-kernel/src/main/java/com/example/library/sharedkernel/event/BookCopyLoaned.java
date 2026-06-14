package com.example.library.sharedkernel.event;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;

public class BookCopyLoaned extends DomainEvent {

  private final LoanId loanId;
  private final ReaderId readerId;
  private final BookCopyId bookCopyId;

  public BookCopyLoaned(LoanId loanId, ReaderId readerId, BookCopyId bookCopyId) {
    super();
    this.loanId = loanId;
    this.readerId = readerId;
    this.bookCopyId = bookCopyId;
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
}
