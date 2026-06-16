package com.example.library.catalog.application.repository;

import com.example.library.catalog.application.port.out.BookPersistencePort;
import com.example.library.catalog.domain.book.Book;
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
