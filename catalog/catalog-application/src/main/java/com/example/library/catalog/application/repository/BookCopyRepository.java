package com.example.library.catalog.application.repository;

import com.example.library.catalog.application.port.out.BookCopyPersistencePort;
import com.example.library.catalog.domain.copy.BookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import java.util.Optional;

public class BookCopyRepository {

  private final BookCopyPersistencePort persistencePort;

  public BookCopyRepository(BookCopyPersistencePort persistencePort) {
    this.persistencePort = persistencePort;
  }

  public void create(BookCopy bookCopy) {
    persistencePort.create(bookCopy);
  }

  public Optional<BookCopy> find(BookCopyId bookCopyId) {
    return persistencePort.find(bookCopyId);
  }

  public void update(BookCopy bookCopy) {
    persistencePort.update(bookCopy);
  }
}
