package com.example.library.catalog.application.repository;

import com.example.library.catalog.application.port.out.BookPersistencePort;
import com.example.library.catalog.domain.book.Book;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.util.Optional;

public class BookRepository {

  private final BookPersistencePort persistencePort;

  public BookRepository(BookPersistencePort persistencePort) {
    this.persistencePort = persistencePort;
  }

  public void create(Book book) {
    persistencePort.create(book);
  }

  public Optional<Book> find(BookId bookId) {
    return persistencePort.find(bookId);
  }

  public Optional<Book> findByISBN(ISBN isbn) {
    return persistencePort.findByISBN(isbn);
  }
}
