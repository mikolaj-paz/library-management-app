package com.example.library.lending.application.port.out;

import com.example.library.sharedkernel.identifier.BookId;

public interface BookRepository {

  boolean existsReaderInQueue(BookId bookId);
}
