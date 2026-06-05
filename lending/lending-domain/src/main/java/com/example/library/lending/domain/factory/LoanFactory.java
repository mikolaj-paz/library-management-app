package com.example.library.lending.domain.factory;

import com.example.library.lending.domain.loan.Loan;
import com.example.library.lending.domain.loan.LoanId;
import com.example.library.lending.domain.loan.LoanPeriod;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.PatronId;

public class LoanFactory {

  public Loan open(LoanId id, PatronId patronId, BookCopyId bookCopyId, LoanPeriod period) {
    // This should also register the event in the domain event system
    return new Loan(id, bookCopyId, patronId, period);
  }
}
