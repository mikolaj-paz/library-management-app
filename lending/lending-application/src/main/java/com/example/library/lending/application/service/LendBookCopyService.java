package com.example.library.lending.application.service;

import com.example.library.lending.application.command.LendBookCopy;
import com.example.library.lending.application.port.in.ILendBookCopy;
import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.lending.application.repository.LoanRepository;
import com.example.library.lending.application.repository.ReaderRepository;
import com.example.library.lending.domain.copy.BookCopy;
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

  private void verifyReaderEligibility(ReaderId readerId, BookCopy bookCopy) {
    var reader =
        readerRepository
            .find(readerId)
            .orElseThrow(() -> new IllegalArgumentException("Reader not found: " + readerId));

    if (reader.isBlocked()) throw new ReaderBlockedException(readerId);

    int activeLoans = loanRepository.countActiveLoansForReader(readerId);
    if (activeLoans >= LoanLimitExceededException.MAX_ACTIVE_LOANS)
      throw new LoanLimitExceededException(readerId);

    bookCopy.verifyCanBeLoanedBy(readerId);
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

    // 2. System weryfikuje w bazie danych możliwość wypożyczenia książki przez tego czytelnika.
    var bookCopy =
        bookCopyRepository
            .find(bookCopyId)
            .orElseThrow(() -> new IllegalArgumentException("Book copy not found: " + bookCopyId));
    verifyReaderEligibility(readerId, bookCopy);

    // 3. Utworzenie nowego obiektu wypożyczenia oraz automatyczne wyznaczenie terminu zwrotu.
    var loan = loanFactory.create(command.readerId(), command.bookCopyId());
    var loanId = loan.id();

    // 4. Zapisanie nowego wypożyczenia w bazie danych.
    loanRepository.create(loan);

    // 5. Status egzemplarza jest ustawiany na “Wypożyczony”.
    bookCopy.lend(loan.readerId(), loanId);

    // 6. Aktualizacja statusu egzemplarza w bazie danych.
    bookCopyRepository.update(bookCopy);

    bookCopy.pullDomainEvents().forEach(eventPublisher::publish);

    return loanId;
  }
}
