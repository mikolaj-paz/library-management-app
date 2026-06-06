package com.example.library.lending.domain.loan;

import java.util.Objects;
import java.util.UUID;

public record LoanId(UUID value) {

  public LoanId {
    Objects.requireNonNull(value, "Loan ID must not be null");
  }

  public static LoanId create() {
    return new LoanId(UUID.randomUUID());
  }

  public static LoanId of(String id) {
    return new LoanId(UUID.fromString(id));
  }
}
