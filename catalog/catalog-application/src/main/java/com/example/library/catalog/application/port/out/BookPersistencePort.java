package com.example.library.catalog.application.port.out;

import com.example.library.catalog.domain.book.Book;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.util.Optional;

public interface BookPersistencePort {

  void create(Book book);

  Optional<Book> find(BookId bookId);

  Optional<Book> findByISBN(ISBN isbn);
}
