package com.example.library.lending.application.command;

import com.example.library.lending.domain.loan.LoanPeriod;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.PatronId;
import java.time.LocalDate;
import java.util.Objects;

public record LendBookCopy(
    BookCopyId copyId, PatronId patronId, LocalDate startDate, LocalDate dueDate) {

  public LendBookCopy {
    Objects.requireNonNull(copyId, "Copy ID must not be null");
    Objects.requireNonNull(patronId, "Patron ID must not be null");
    Objects.requireNonNull(startDate, "Start date must not be null");
    Objects.requireNonNull(dueDate, "Due date must not be null");
  }

  public LoanPeriod loanPeriod() {
    return new LoanPeriod(startDate, dueDate);
  }
}
