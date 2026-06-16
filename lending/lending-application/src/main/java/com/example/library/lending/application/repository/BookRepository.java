package com.example.library.lending.application.repository;

import com.example.library.lending.application.port.out.BookPersistencePort;
import com.example.library.lending.domain.book.Book;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.Optional;

public class BookRepository {

  private final BookPersistencePort persistencePort;

  public BookRepository(BookPersistencePort persistencePort) {
    this.persistencePort = persistencePort;
  }

  public Optional<Book> find(BookId bookId) {
    return persistencePort.find(bookId);
  }
}
