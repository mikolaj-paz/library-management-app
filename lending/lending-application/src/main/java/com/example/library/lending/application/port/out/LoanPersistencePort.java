package com.example.library.lending.application.port.out;

import com.example.library.lending.application.query.LoanSummary;
import com.example.library.lending.domain.loan.Loan;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.List;
import java.util.Optional;

public interface LoanPersistencePort {

  void create(Loan loan);

  void update(Loan loan);

  Optional<Loan> findActiveLoan(BookCopyId bookCopyId);

  Optional<Loan> find(LoanId loanId);

  List<LoanSummary> findFor(ReaderId readerId);
}
