package com.example.library.lending.domain.loan;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.PatronId;
import com.example.library.sharedkernel.primitives.AggregateRoot;

public class Loan extends AggregateRoot<LoanId> {

  private final BookCopyId copyId;
  private final PatronId patronId;
  private LoanPeriod period;

  public Loan(LoanId id, BookCopyId copyId, PatronId patronId, LoanPeriod period) {
    super(id);
    this.copyId = copyId;
    this.patronId = patronId;
    this.period = period;
  }
}
