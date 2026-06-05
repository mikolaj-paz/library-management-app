package com.example.library.domain.lending.loan;

import java.util.Objects;
import java.util.UUID;

public record LoanId(UUID id) {

  public LoanId {
    Objects.requireNonNull(id, "Loan ID must not be null");
  }
}
