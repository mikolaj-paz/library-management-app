package com.example.library.lending.domain.loan;

import java.util.Objects;
import java.util.UUID;

public record LoanId(UUID id) {

  public LoanId {
    Objects.requireNonNull(id, "Loan ID must not be null");
  }
}
