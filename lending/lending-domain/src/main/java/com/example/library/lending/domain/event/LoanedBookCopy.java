package com.example.library.lending.domain.event;

import com.example.library.lending.domain.loan.LoanId;
import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public record LoanedBookCopy(
    LoanId loanId, BookCopyId copyId, ReaderId readerId, LocalDate returnDate, Instant occurredOn)
    implements DomainEvent {

  public LoanedBookCopy {
    Objects.requireNonNull(loanId, "Loan ID must not be null");
    Objects.requireNonNull(copyId, "Copy ID must not be null");
    Objects.requireNonNull(readerId, "Reader ID must not be null");
    Objects.requireNonNull(returnDate, "Return date must not be null");
    Objects.requireNonNull(occurredOn, "Event occurrence date must not be null");
  }
}
