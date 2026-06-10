package com.example.library.lending.application.service;

import com.example.library.lending.application.command.ExtendLoanCommand;
import com.example.library.lending.application.port.in.IExtendLoan;
import com.example.library.lending.application.port.out.BookCopyRepository;
import com.example.library.lending.application.port.out.BookRepository;
import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.domain.exception.ExtensionNotAllowedException;
import com.example.library.sharedkernel.event.LoanExtended;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;

public class ExtendLoanService implements IExtendLoan {

  private final LoanRepository loanRepository;
  private final BookRepository bookRepository;
  private final BookCopyRepository bookCopyRepository;
  private final DomainEventPublisher eventPublisher;

  public ExtendLoanService(
      LoanRepository loanRepository,
      BookRepository bookRepository,
      BookCopyRepository bookCopyRepository,
      DomainEventPublisher eventPublisher) {
    this.loanRepository = loanRepository;
    this.bookRepository = bookRepository;
    this.bookCopyRepository = bookCopyRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void extend(ExtendLoanCommand command) {
    var loanId = command.loanId();
    var readerId = command.readerId();

    var loan =
        loanRepository
            .find(loanId)
            .orElseThrow(() -> new IllegalArgumentException("Loan not found: " + loanId));

    if (!loan.readerId().equals(readerId)) {
      throw new IllegalArgumentException(
          "Loan " + loanId + " does not belong to reader: " + readerId);
    }

    var bookCopyId = loan.bookCopyId();
    var bookCopy =
        bookCopyRepository
            .find(bookCopyId)
            .orElseThrow(() -> new IllegalStateException("Book copy not found: " + bookCopyId));

    if (bookRepository.existsReaderInQueue(bookCopy.bookId())) {
      throw new ExtensionNotAllowedException(
          "Cannot extend loan " + loanId + " because there are readers in the queue.");
    }

    loan.extend();

    loanRepository.update(loan);

    eventPublisher.publish(new LoanExtended(loanId, readerId, bookCopyId, loan.dueDate()));
  }
}
