package com.example.library.catalog.domain.book;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;

public class BookFactoryImpl implements BookFactory {

  @Override
  public Book create(
      String title, String author, ISBN isbn, String publisher, LocalDate publicationDate) {
    return Book.of(BookId.create(), title, author, isbn, publisher, publicationDate);
  }

  @Override
  public Book reconstitute(
      BookId id,
      String title,
      String author,
      ISBN isbn,
      String publisher,
      LocalDate publicationDate) {
    return Book.of(id, title, author, isbn, publisher, publicationDate);
  }
}
