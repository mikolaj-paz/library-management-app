package com.example.library.lending.application.service;

import com.example.library.lending.application.command.ExtendLoanCommand;
import com.example.library.lending.application.port.in.IExtendLoan;
import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.lending.application.repository.BookRepository;
import com.example.library.lending.application.repository.LoanRepository;
import com.example.library.lending.domain.exception.ExtensionNotAllowedException;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;

public class ExtendingLoan implements IExtendLoan {

  private final LoanRepository loanRepository;
  private final BookRepository bookRepository;
  private final BookCopyRepository bookCopyRepository;
  private final DomainEventPublisher eventPublisher;

  public ExtendingLoan(
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
  public void extendLoan(ExtendLoanCommand command) {
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

    var book =
        bookRepository
            .find(bookCopy.bookId())
            .orElseThrow(() -> new IllegalStateException("Book not found: " + bookCopy.bookId()));

    // 2. Sprawdzenie w bazie danych warunków pozwalających na przedłużenie (brak kolejki, nie
    // przekroczony limit przedłużeń).
    if (book.hasQueuedReader()) {
      throw new ExtensionNotAllowedException(
          "Cannot extend loan " + loanId + " because there are readers in the queue.");
    }

    // 3. Obliczenie nowego terminu zwrotu książki.
    loan.extend();

    // 4. Aktualizacja terminu zwrotu w bazie danych dla danego wypożyczenia.
    loanRepository.update(loan);

    loan.pullDomainEvents().forEach(eventPublisher::publish);
  }
}
