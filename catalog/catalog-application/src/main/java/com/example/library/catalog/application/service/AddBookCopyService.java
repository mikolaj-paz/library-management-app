package com.example.library.catalog.application.service;

import com.example.library.catalog.application.command.AddBookCopy;
import com.example.library.catalog.application.port.in.IAddBookCopy;
import com.example.library.catalog.application.port.out.BookCopyRepository;
import com.example.library.catalog.application.port.out.BookRepository;
import com.example.library.catalog.domain.copy.BookCopy;
import com.example.library.catalog.domain.copy.BookCopyAdded;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;

public class AddBookCopyService implements IAddBookCopy {

  private final BookCopyRepository bookCopyRepository;
  private final BookRepository bookRepository;
  private final DomainEventPublisher eventPublisher;

  public AddBookCopyService(
      BookCopyRepository bookCopyRepository,
      BookRepository bookRepository,
      DomainEventPublisher eventPublisher) {
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

    BookCopy bookCopy = BookCopy.create(command.bookId());
    var bookCopyId = bookCopy.id();

    bookCopyRepository.create(bookCopy);

    eventPublisher.publish(new BookCopyAdded(bookCopyId, bookId));

    return bookCopyId;
  }
}
