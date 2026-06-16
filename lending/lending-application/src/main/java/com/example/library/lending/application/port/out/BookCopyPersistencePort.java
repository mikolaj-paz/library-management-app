package com.example.library.lending.application.port.out;

import com.example.library.lending.domain.copy.BookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.Optional;

public interface BookCopyPersistencePort {

  void update(BookCopy bookCopy);

  Optional<BookCopy> find(BookCopyId id);

  Optional<BookCopy> findAvailableBookCopy(BookId bookId);
}
