package com.example.library.lending.domain.event;

import com.example.library.lending.domain.loan.LoanId;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.PatronId;
import com.example.library.sharedkernel.primitives.DomainEvent;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public record LoanedBookCopy(
    LoanId loanId,
    BookCopyId copyId,
    PatronId patronId,
    LocalDate loanStartDate,
    LocalDate dueDate,
    Instant occurredOn)
    implements DomainEvent {

  public LoanedBookCopy {
    Objects.requireNonNull(loanId, "Loan ID must not be null");
    Objects.requireNonNull(copyId, "Copy ID must not be null");
    Objects.requireNonNull(patronId, "Patron ID must not be null");
    Objects.requireNonNull(loanStartDate, "Loan start date must not be null");
    Objects.requireNonNull(dueDate, "Due date must not be null");
    Objects.requireNonNull(occurredOn, "Event occurrence date must not be null");
  }
}
