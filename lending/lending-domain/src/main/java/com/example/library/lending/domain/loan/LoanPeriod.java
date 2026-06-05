package com.example.library.lending.domain.loan;

import java.time.LocalDate;

public record LoanPeriod(LocalDate startDate, LocalDate endDate) {

  public LoanPeriod {
    if (startDate == null || endDate == null) {
      throw new IllegalArgumentException("Start date and end date must not be null");
    }

    if (endDate.isBefore(startDate)) {
      throw new IllegalArgumentException("End date must be after start date");
    }
  }
}
