package com.example.library.lending.application.port.out;

import com.example.library.lending.domain.loan.Loan;
import com.example.library.sharedkernel.identifier.ReaderId;

public interface LoanRepository {

  void create(Loan loan);

  int countActiveLoansForReader(ReaderId readerId);
}
