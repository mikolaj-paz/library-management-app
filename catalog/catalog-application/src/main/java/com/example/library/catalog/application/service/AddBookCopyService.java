package com.example.library.catalog.application.service;

import com.example.library.catalog.application.command.AddBookCopy;
import com.example.library.catalog.application.port.in.IAddBookCopy;
import com.example.library.catalog.application.repository.BookCopyRepository;
import com.example.library.catalog.application.repository.BookRepository;
import com.example.library.catalog.domain.copy.BookCopy;
import com.example.library.catalog.domain.copy.BookCopyFactory;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;

public class AddBookCopyService implements IAddBookCopy {

  private final BookCopyFactory bookCopyFactory;
  private final BookCopyRepository bookCopyRepository;
  private final BookRepository bookRepository;
  private final DomainEventPublisher eventPublisher;

  public AddBookCopyService(
      BookCopyFactory bookCopyFactory,
      BookCopyRepository bookCopyRepository,
      BookRepository bookRepository,
      DomainEventPublisher eventPublisher) {
    this.bookCopyFactory = bookCopyFactory;
    this.bookCopyRepository = bookCopyRepository;
    this.bookRepository = bookRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public BookCopyId add(AddBookCopy command) {
    var bookId = command.bookId();
    bookRepository
        .find(bookId)
        .orElseThrow(() -> new IllegalArgumentException("Book with id " + bookId + " not found"));

    // 2. Tworzenie nowego obiektu egzemplarza w systemie i ustawienie jego statusu na “Dostępny”.
    BookCopy bookCopy = bookCopyFactory.create(command.bookId());

    // 3. Zapisanie nowego egzemplarza w bazie danych zasobów biblioteki.
    bookCopyRepository.create(bookCopy);

    bookCopy.pullDomainEvents().forEach(eventPublisher::publish);

    return bookCopy.id();
  }
}
