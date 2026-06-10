package com.example.library.catalog.domain.book;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;

public class BookFactoryImpl implements BookFactory {

  @Override
  public Book create(String title, String author, ISBN isbn) {
    return Book.of(BookId.create(), title, author, isbn, null);
  }

  @Override
  public Book reconstitute(
      BookId id, String title, String author, ISBN isbn, ReaderId queuedReaderId) {
    return Book.of(id, title, author, isbn, queuedReaderId);
  }
}
