package com.example.library.lending.application.service;

import com.example.library.lending.application.command.ReturnBookCopy;
import com.example.library.lending.application.port.in.IReturnBookCopy;
import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.lending.application.repository.LoanRepository;
import com.example.library.lending.domain.exception.LoanNotFoundException;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import java.time.LocalDate;

public class ReturnBookCopyService implements IReturnBookCopy {

  private final LoanRepository loanRepository;
  private final BookCopyRepository bookCopyRepository;
  private final DomainEventPublisher eventPublisher;

  public ReturnBookCopyService(
      LoanRepository loanRepository,
      BookCopyRepository bookCopyRepository,
      DomainEventPublisher eventPublisher) {
    this.loanRepository = loanRepository;
    this.bookCopyRepository = bookCopyRepository;
    this.eventPublisher = eventPublisher;
  }

  // ============================ NOTE =============================
  // There is no account blocking currently implemented. If we still
  // want to keep penalties, this use case needs alignment.
  // ===============================================================
  @Override
  public void returnCopy(ReturnBookCopy command) {
    var bookCopyId = command.bookCopyId();
    var today = LocalDate.now();

    var loan =
        loanRepository
            .findActiveLoan(bookCopyId)
            .orElseThrow(() -> new LoanNotFoundException(bookCopyId));

    boolean isOverdue = loan.isOverdue(today);

    loan.close();

    var bookCopy =
        bookCopyRepository
            .find(bookCopyId)
            .orElseThrow(() -> new IllegalArgumentException("Book copy not found: " + bookCopyId));

    bookCopy.returnIt(loan.readerId(), isOverdue);

    loanRepository.update(loan);
    bookCopyRepository.update(bookCopy);

    bookCopy.pullDomainEvents().forEach(eventPublisher::publish);
  }
}
