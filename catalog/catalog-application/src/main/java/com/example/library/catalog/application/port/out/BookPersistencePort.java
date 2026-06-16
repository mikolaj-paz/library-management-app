package com.example.library.catalog.application.port.out;

import com.example.library.catalog.domain.book.Book;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.Optional;

public interface BookPersistencePort {

  Optional<Book> find(BookId bookId);
}
