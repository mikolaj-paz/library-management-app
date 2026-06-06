package com.example.library.lending.application.port.out;

import com.example.library.lending.domain.loan.Loan;
import com.example.library.lending.domain.loan.LoanId;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.PatronId;
import com.example.library.sharedkernel.primitives.DomainEvent;
import com.example.library.sharedkernel.repository.Repository;
import java.util.List;

public interface LoanRepository extends Repository<Loan, LoanId> {

  boolean existsActiveLoanForCopy(BookCopyId copyId);

  int countActiveLoansForPatron(PatronId patronId);

  boolean isPatronBlocked(PatronId patronId);

  boolean isCopyAvailable(BookCopyId copyId);

  boolean isCopyReservedForPatron(BookCopyId copyId, PatronId patronId);

  void markCopyAsLoaned(BookCopyId copyId);

  void publishDomainEvents(List<DomainEvent> domainEvents);
}
