package com.example.library.lending.application.repository;

import com.example.library.lending.application.port.out.LoanPersistencePort;
import com.example.library.lending.application.query.LoanSummary;
import com.example.library.lending.domain.loan.Loan;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.List;
import java.util.Optional;

public class LoanRepository {

  private final LoanPersistencePort persistencePort;

  public LoanRepository(LoanPersistencePort persistencePort) {
    this.persistencePort = persistencePort;
  }

  public void create(Loan loan) {
    persistencePort.create(loan);
  }

  public void update(Loan loan) {
    persistencePort.update(loan);
  }

  public Optional<Loan> findActiveLoan(BookCopyId bookCopyId) {
    return persistencePort.findActiveLoan(bookCopyId);
  }

  public Optional<Loan> find(LoanId loanId) {
    return persistencePort.find(loanId);
  }

  public List<LoanSummary> findFor(ReaderId readerId) {
    return persistencePort.findFor(readerId);
  }
}
