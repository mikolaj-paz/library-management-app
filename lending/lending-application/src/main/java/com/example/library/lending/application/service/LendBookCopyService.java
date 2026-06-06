package com.example.library.lending.application.service;

import com.example.library.lending.application.command.LendBookCopy;
import com.example.library.lending.application.port.in.ILendBookCopy;
import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.domain.loan.Loan;
import com.example.library.lending.domain.loan.LoanFactory;
import com.example.library.lending.domain.loan.LoanId;
import java.util.Objects;

public class LendBookCopyService implements ILendBookCopy {

  private static final int MAX_ACTIVE_LOANS = 5;

  private final LoanRepository loanRepository;
  private final LoanFactory loanFactory;

  public LendBookCopyService(LoanRepository loanRepository, LoanFactory loanFactory) {
    this.loanRepository =
        Objects.requireNonNull(loanRepository, "Loan repository must not be null");
    this.loanFactory = Objects.requireNonNull(loanFactory, "Loan factory must not be null");
  }

  @Override
  public void lendBookCopy(LendBookCopy command) {
    Objects.requireNonNull(command, "Lend command must not be null");

    if (loanRepository.isPatronBlocked(command.patronId())) {
      throw new IllegalStateException("Patron account is blocked");
    }

    if (loanRepository.countActiveLoansForPatron(command.patronId()) >= MAX_ACTIVE_LOANS) {
      throw new IllegalStateException("Patron reached active loan limit");
    }

    if (loanRepository.existsActiveLoanForCopy(command.copyId())) {
      throw new IllegalStateException("Book copy already has an active loan");
    }

    boolean available = loanRepository.isCopyAvailable(command.copyId());
    boolean reservedForPatron =
        loanRepository.isCopyReservedForPatron(command.copyId(), command.patronId());
    if (!available && !reservedForPatron) {
      throw new IllegalStateException("Book copy is not available for this patron");
    }

    Loan loan =
        loanFactory.open(
            LoanId.newId(), command.patronId(), command.copyId(), command.loanPeriod());
    loanRepository.save(loan);
    loanRepository.markCopyAsLoaned(command.copyId());
    loanRepository.publishDomainEvents(loan.pullDomainEvents());
  }
}
