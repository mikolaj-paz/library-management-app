package com.example.library.lending.application.repository;

import com.example.library.lending.application.port.out.BookCopyPersistencePort;
import com.example.library.lending.domain.copy.BookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.Optional;

public class BookCopyRepository {

  private final BookCopyPersistencePort persistencePort;

  public BookCopyRepository(BookCopyPersistencePort persistencePort) {
    this.persistencePort = persistencePort;
  }

  public void update(BookCopy bookCopy) {
    persistencePort.update(bookCopy);
  }

  public Optional<BookCopy> find(BookCopyId id) {
    return persistencePort.find(id);
  }

  public Optional<BookCopy> findAvailableBookCopy(BookId bookId) {
    return persistencePort.findAvailableBookCopy(bookId);
  }
}
