package com.example.library.lending.domain.loan;

import com.example.library.lending.domain.event.LoanedBookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.PatronId;
import com.example.library.sharedkernel.primitives.AggregateRoot;
import java.time.Instant;
import java.time.LocalDate;
import java.util.Objects;

public class Loan extends AggregateRoot<LoanId> {

  private final BookCopyId copyId;
  private final PatronId patronId;
  private final LoanPeriod period;
  private LocalDate returnedOn;

  public Loan(LoanId id, BookCopyId copyId, PatronId patronId, LoanPeriod period) {
    super(id);
    this.copyId = Objects.requireNonNull(copyId, "Copy ID must not be null");
    this.patronId = Objects.requireNonNull(patronId, "Patron ID must not be null");
    this.period = Objects.requireNonNull(period, "Loan period must not be null");
    registerEvent(
        new LoanedBookCopy(
            id(), copyId, patronId, period.startDate(), period.endDate(), Instant.now()));
  }

  public BookCopyId copyId() {
    return copyId;
  }

  public PatronId patronId() {
    return patronId;
  }

  public LoanPeriod period() {
    return period;
  }

  public LocalDate returnedOn() {
    return returnedOn;
  }

  public boolean isActive() {
    return returnedOn == null;
  }

  public void returnCopy(LocalDate returnDate) {
    Objects.requireNonNull(returnDate, "Return date must not be null");
    if (!isActive()) {
      throw new IllegalStateException("Loan is already returned");
    }
    if (returnDate.isBefore(period.startDate())) {
      throw new IllegalArgumentException("Return date cannot be before loan start date");
    }
    returnedOn = returnDate;
  }
}
