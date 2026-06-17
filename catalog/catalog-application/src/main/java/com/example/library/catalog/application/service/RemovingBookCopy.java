package com.example.library.catalog.application.service;

import com.example.library.catalog.application.command.RemoveBookCopy;
import com.example.library.catalog.application.port.in.IRemoveBookCopy;
import com.example.library.catalog.application.repository.BookCopyRepository;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;

public class RemovingBookCopy implements IRemoveBookCopy {

  private final BookCopyRepository bookCopyRepository;
  private final DomainEventPublisher eventPublisher;

  public RemovingBookCopy(
      BookCopyRepository bookCopyRepository, DomainEventPublisher eventPublisher) {
    this.bookCopyRepository = bookCopyRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void removeBookCopy(RemoveBookCopy command) {
    var bookCopyId = command.bookCopyId();
    var bookCopy =
        bookCopyRepository
            .find(bookCopyId)
            .orElseThrow(
                () -> new IllegalArgumentException("Book copy not found: " + bookCopyId.value()));

    // 2. Weryfikacja w bazie danych warunków wstępnych (czy egzemplarz nie jest aktualnie
    // wypożyczony bądź zarezerwowany).
    // 3. Status wybranego egzemplarza zostaje ustawiony na „Wycofany”.
    bookCopy.remove();

    // 4. Aktualizacja statusu obiektu i zapisanie zmian w bazie danych.
    bookCopyRepository.update(bookCopy);

    bookCopy.pullDomainEvents().forEach(eventPublisher::publish);
  }
}
