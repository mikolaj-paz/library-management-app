package com.example.library.lending.domain.loan;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.PatronId;
import java.util.Objects;

public class LoanFactory {

  public Loan open(LoanId id, PatronId patronId, BookCopyId bookCopyId, LoanPeriod period) {
    Objects.requireNonNull(id, "Loan ID must not be null");
    Objects.requireNonNull(patronId, "Patron ID must not be null");
    Objects.requireNonNull(bookCopyId, "Book copy ID must not be null");
    Objects.requireNonNull(period, "Loan period must not be null");
    return new Loan(id, bookCopyId, patronId, period);
  }
}
