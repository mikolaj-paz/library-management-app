package com.example.library.lending.application.service;

import com.example.library.lending.application.command.JoinWaitingQueue;
import com.example.library.lending.application.port.in.IJoinWaitingQueue;
import com.example.library.lending.application.repository.BookRepository;
import com.example.library.lending.domain.exception.BookAlreadyInReaderWaitingQueueException;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;

public class JoiningWaitingQueue implements IJoinWaitingQueue {

  private final BookRepository bookRepository;
  private final DomainEventPublisher eventPublisher;

  public JoiningWaitingQueue(BookRepository bookRepository, DomainEventPublisher eventPublisher) {
    this.bookRepository = bookRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void joinWaitingQueue(JoinWaitingQueue command) {
    var bookId = command.bookId();
    var readerId = command.readerId();

    // 3. Sprawdzenie w bazie danych, czy czytelnik nie posiada już aktywnego wpisu w kolejce dla
    // tej konkretnej książki.
    var book =
        bookRepository
            .find(bookId)
            .orElseThrow(() -> new IllegalArgumentException("Book not found: " + bookId));
    if (book.hasQueued(readerId)) {
      throw new BookAlreadyInReaderWaitingQueueException(readerId, bookId);
    }

    // 4. Dodanie nowego wpisu o rezerwacji kolejki dla czytelnika.
    book.addToQueue(readerId);

    // 5. Zapisanie wpisu kolejki w bazie danych.
    bookRepository.update(book);

    book.pullDomainEvents().forEach(eventPublisher::publish);
  }
}
