package com.example.library.lending.application.service;

import com.example.library.lending.application.command.LendBookCopy;
import com.example.library.lending.application.port.in.ILendBookCopy;
import com.example.library.lending.application.port.out.BookCopyRepository;
import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.application.port.out.ReaderRepository;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.lending.domain.loan.BookCopyLoaned;
import com.example.library.lending.domain.loan.Loan;
import com.example.library.lending.domain.loan.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import java.util.Objects;

public class LendBookCopyService implements ILendBookCopy {

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
      DomainEventPublisher eventPublisher) {
    this.loanRepository =
        Objects.requireNonNull(loanRepository, "Loan repository must not be null");
    this.bookCopyRepository =
        Objects.requireNonNull(bookCopyRepository, "Book copy repository must not be null");
    this.readerRepository =
        Objects.requireNonNull(readerRepository, "Reader repository must not be null");
    this.eventPublisher =
        Objects.requireNonNull(eventPublisher, "Event publisher must not be null");
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

    var loan = Loan.create(command.readerId(), command.bookCopyId());
    var loanId = loan.id();

    bookCopy.updateStatusAsLoaned();

    loanRepository.create(loan);
    bookCopyRepository.update(bookCopy);

    eventPublisher.publish(new BookCopyLoaned(loanId, readerId, bookCopyId, loan.dueDate()));

    return loanId;
  }
}
