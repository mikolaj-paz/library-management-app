package com.example.library.domain.factory;

import com.example.library.domain.model.BookCopy;
import com.example.library.domain.model.Loan;
import com.example.library.domain.model.LoanId;
import com.example.library.domain.model.LoanPeriod;
import com.example.library.domain.model.LoanStatus;
import com.example.library.domain.model.Patron;

public class LoanFactory {

  public Loan open(LoanId id, Patron patron, BookCopy bookCopy, LoanPeriod period) {
    // This should also register the event in the domain event system
    return new Loan(id, bookCopy.id(), patron.id(), LoanStatus.ACTIVE, period);
  }
}
