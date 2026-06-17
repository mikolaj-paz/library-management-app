package com.example.library.lending.application.port.out;

import com.example.library.lending.domain.book.Book;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.Optional;

public interface BookPersistencePort {

  Optional<Book> find(BookId bookId);

  void update(Book book);
}
