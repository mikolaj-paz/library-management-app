package com.example.library.lending.application.service;

import com.example.library.lending.application.command.LendBookCopy;
import com.example.library.lending.application.port.in.ILendBookCopy;
import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.lending.application.repository.LoanRepository;
import com.example.library.lending.application.repository.ReaderRepository;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.lending.domain.loan.LoanFactory;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;

public class LendBookCopyService implements ILendBookCopy {

  private final LoanFactory loanFactory;
  private final LoanRepository loanRepository;
  private final BookCopyRepository bookCopyRepository;
  private final ReaderRepository readerRepository;
  private final DomainEventPublisher eventPublisher;

  private void verifyLoanLimitNotExceededBy(ReaderId readerId) {
    int activeLoans = loanRepository.countActiveLoansForReader(readerId);
    if (activeLoans >= LoanLimitExceededException.MAX_ACTIVE_LOANS) {
      throw new LoanLimitExceededException(readerId);
    }
  }

  public LendBookCopyService(
      LoanRepository loanRepository,
      BookCopyRepository bookCopyRepository,
      ReaderRepository readerRepository,
      LoanFactory loanFactory,
      DomainEventPublisher eventPublisher) {
    this.loanRepository = loanRepository;
    this.bookCopyRepository = bookCopyRepository;
    this.loanFactory = loanFactory;
    this.readerRepository = readerRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public LoanId lend(LendBookCopy command) {
    var readerId = command.readerId();
    var bookCopyId = command.bookCopyId();

    var reader =
        readerRepository
            .find(readerId)
            .orElseThrow(() -> new IllegalArgumentException("Reader not found: " + readerId));

    if (reader.isBlocked()) {
      throw new ReaderBlockedException(readerId);
    }

    verifyLoanLimitNotExceededBy(readerId);

    var bookCopy =
        bookCopyRepository
            .find(bookCopyId)
            .orElseThrow(() -> new IllegalArgumentException("Book copy not found: " + bookCopyId));
    bookCopy.verifyCanBeLoanedBy(readerId);

    var loan = loanFactory.create(command.readerId(), command.bookCopyId());
    var loanId = loan.id();

    bookCopy.lend(loan.readerId(), loanId);

    loanRepository.create(loan);
    bookCopyRepository.update(bookCopy);

    bookCopy.pullDomainEvents().forEach(eventPublisher::publish);

    return loanId;
  }
}
