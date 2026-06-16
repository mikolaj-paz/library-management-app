package com.example.library.catalog.application.port.out;

import com.example.library.catalog.domain.copy.BookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import java.util.Optional;

public interface BookCopyPersistencePort {

  void create(BookCopy bookCopy);

  Optional<BookCopy> find(BookCopyId bookCopyId);

  void update(BookCopy bookCopy);
}
