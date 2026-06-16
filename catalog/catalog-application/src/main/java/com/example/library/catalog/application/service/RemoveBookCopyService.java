package com.example.library.catalog.application.service;

import com.example.library.catalog.application.command.RemoveBookCopy;
import com.example.library.catalog.application.port.in.IRemoveBookCopy;
import com.example.library.catalog.application.repository.BookCopyRepository;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;

public class RemoveBookCopyService implements IRemoveBookCopy {

  private final BookCopyRepository bookCopyRepository;
  private final DomainEventPublisher eventPublisher;

  public RemoveBookCopyService(
      BookCopyRepository bookCopyRepository, DomainEventPublisher eventPublisher) {
    this.bookCopyRepository = bookCopyRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void remove(RemoveBookCopy command) {
    var bookCopyId = command.bookCopyId();
    var bookCopy =
        bookCopyRepository
            .find(bookCopyId)
            .orElseThrow(
                () -> new IllegalArgumentException("Book copy not found: " + bookCopyId.value()));

    bookCopy.remove();

    bookCopyRepository.update(bookCopy);

    bookCopy.pullDomainEvents().forEach(eventPublisher::publish);
  }
}
