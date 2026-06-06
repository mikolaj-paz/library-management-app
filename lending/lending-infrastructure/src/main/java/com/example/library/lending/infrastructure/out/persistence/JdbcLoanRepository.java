package com.example.library.lending.infrastructure.out.persistence;

import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.domain.copy.BookCopyStatus;
import com.example.library.lending.domain.loan.Loan;
import com.example.library.lending.domain.loan.LoanId;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.PatronId;
import com.example.library.sharedkernel.primitives.DomainEvent;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class JdbcLoanRepository implements LoanRepository {

  private final Map<LoanId, Loan> loans = new ConcurrentHashMap<>();
  private final Map<BookCopyId, BookCopyStatus> copyStatuses = new ConcurrentHashMap<>();
  private final Map<BookCopyId, PatronId> reservationOwners = new ConcurrentHashMap<>();
  private final Set<PatronId> blockedPatrons = ConcurrentHashMap.newKeySet();

  @Override
  public Optional<Loan> findById(LoanId id) {
    return Optional.ofNullable(loans.get(id));
  }

  @Override
  public void save(Loan aggregate) {
    loans.put(aggregate.id(), aggregate);
  }

  @Override
  public boolean existsActiveLoanForCopy(BookCopyId copyId) {
    return loans.values().stream()
        .anyMatch(loan -> loan.isActive() && loan.copyId().equals(copyId));
  }

  @Override
  public int countActiveLoansForPatron(PatronId patronId) {
    return (int)
        loans.values().stream()
            .filter(loan -> loan.isActive() && loan.patronId().equals(patronId))
            .count();
  }

  @Override
  public boolean isPatronBlocked(PatronId patronId) {
    return blockedPatrons.contains(patronId);
  }

  @Override
  public boolean isCopyAvailable(BookCopyId copyId) {
    return copyStatuses.getOrDefault(copyId, BookCopyStatus.AVAILABLE) == BookCopyStatus.AVAILABLE;
  }

  @Override
  public boolean isCopyReservedForPatron(BookCopyId copyId, PatronId patronId) {
    return patronId.equals(reservationOwners.get(copyId));
  }

  @Override
  public void markCopyAsLoaned(BookCopyId copyId) {
    copyStatuses.put(copyId, BookCopyStatus.LOANED);
    reservationOwners.remove(copyId);
  }

  @Override
  public void publishDomainEvents(List<DomainEvent> domainEvents) {
    // Placeholder until a dedicated NotificationPort adapter is introduced.
  }
}
