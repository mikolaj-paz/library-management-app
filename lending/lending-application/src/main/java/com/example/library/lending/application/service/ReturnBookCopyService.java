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

    // 2. Pobranie aktywnego wypożyczenia z bazy danych na podstawie danych egzemplarza.
    var loan =
        loanRepository
            .findActiveLoan(bookCopyId)
            .orElseThrow(() -> new LoanNotFoundException(bookCopyId));

    // 3. Weryfikacja terminu zwrotu.
    boolean isOverdue = loan.isOverdue(today);

    // 4. Status wypożyczenia zostaje ustawiony na „Zamknięte”.
    loan.close();

    // 5. Status egzemplarza zostaje zaktualizowany na „Dostępny”.
    var bookCopy =
        bookCopyRepository
            .find(bookCopyId)
            .orElseThrow(() -> new IllegalArgumentException("Book copy not found: " + bookCopyId));
    bookCopy.returnIt(loan.readerId(), isOverdue);

    // 6. Zapisanie zaktualizowanych danych wypożyczenia oraz egzemplarza w bazie danych.
    loanRepository.update(loan);
    bookCopyRepository.update(bookCopy);

    bookCopy.pullDomainEvents().forEach(eventPublisher::publish);
  }
}
