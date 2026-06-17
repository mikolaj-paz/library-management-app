package com.example.library.lending.domain.reader;

import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.identifier.ReaderId;

public class Reader extends AggregateRoot<ReaderId> {

  private final ReaderStatus status;
  private int activeLoansCount;

  private Reader(ReaderId id, ReaderStatus status, int activeLoansCount) {
    super(id);
    this.status = status;
    this.activeLoansCount = activeLoansCount;
  }

  private void verifyEligibility() {
    if (isBlocked()) {
      throw new ReaderBlockedException(id());
    }

    if (activeLoansCount >= LoanLimitExceededException.MAX_ACTIVE_LOANS) {
      throw new LoanLimitExceededException(id());
    }
  }

  static Reader of(ReaderId id, ReaderStatus status, int activeLoansCount) {
    return new Reader(id, status, activeLoansCount);
  }

  public ReaderStatus status() {
    return status;
  }

  public boolean isBlocked() {
    return status == ReaderStatus.BLOCKED;
  }

  public void verifyLoanEligibility() {
    verifyEligibility();
  }

  public void verifyReservationEligibility() {
    verifyEligibility();
  }
}
