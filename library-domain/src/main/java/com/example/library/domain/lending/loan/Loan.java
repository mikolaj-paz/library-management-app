package com.example.library.domain.lending.loan;

import com.example.library.domain.lending.copy.BookCopyId;
import com.example.library.domain.model.PatronId;
import com.example.library.shared.AggregateRoot;

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
