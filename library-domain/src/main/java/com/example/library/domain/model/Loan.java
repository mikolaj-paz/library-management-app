package com.example.library.domain.model;

import com.example.library.shared.AggregateRoot;

public class Loan extends AggregateRoot<LoanId> {

  private final CopyId copyId;
  private final PatronId patronId;
  private LoanStatus status;
  private LoanPeriod period;

  public Loan(LoanId id, CopyId copyId, PatronId patronId, LoanStatus status, LoanPeriod period) {
    super(id);
    this.copyId = copyId;
    this.patronId = patronId;
    this.status = status;
    this.period = period;
  }
}
