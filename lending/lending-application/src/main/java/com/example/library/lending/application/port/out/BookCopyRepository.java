package com.example.library.lending.application.port.out;

import com.example.library.lending.domain.copy.BookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import java.util.Optional;

public interface BookCopyRepository {
  void update(BookCopy bookCopy);

  Optional<BookCopy> findById(BookCopyId id);
}
