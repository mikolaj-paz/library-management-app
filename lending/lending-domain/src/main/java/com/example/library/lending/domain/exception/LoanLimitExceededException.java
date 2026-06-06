package com.example.library.lending.domain.exception;

import com.example.library.sharedkernel.identifier.ReaderId;

public class LoanLimitExceededException extends RuntimeException {

  public static final int MAX_ACTIVE_LOANS = 5;

  public LoanLimitExceededException(ReaderId readerId) {
    super(
        "Reader "
            + readerId.value()
            + " has reached the limit of "
            + MAX_ACTIVE_LOANS
            + " active loans.");
  }
}
