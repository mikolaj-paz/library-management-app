package com.example.library.lending.domain.book;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;

public class BookFactoryImpl implements BookFactory {

  @Override
  public Book reconstitute(BookId id, ReaderId queuedReaderId) {
    return Book.of(id, queuedReaderId);
  }
}
