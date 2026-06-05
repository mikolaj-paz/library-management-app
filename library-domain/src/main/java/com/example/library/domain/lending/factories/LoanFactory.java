package com.example.library.domain.lending.factories;

import com.example.library.domain.lending.copy.BookCopyId;
import com.example.library.domain.lending.loan.Loan;
import com.example.library.domain.lending.loan.LoanId;
import com.example.library.domain.lending.loan.LoanPeriod;
import com.example.library.domain.model.PatronId;

public class LoanFactory {

  public Loan open(LoanId id, PatronId patronId, BookCopyId bookCopyId, LoanPeriod period) {
    // This should also register the event in the domain event system
    return new Loan(id, bookCopyId, patronId, period);
  }
}
