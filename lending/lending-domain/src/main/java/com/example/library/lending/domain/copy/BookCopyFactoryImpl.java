package com.example.library.lending.domain.copy;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;

public class BookCopyFactoryImpl implements BookCopyFactory {

  @Override
  public BookCopy reconstitute(
      BookCopyId id, BookCopyStatus status, ReaderId reservedBy, BookId bookId) {
    return BookCopy.of(id, status, reservedBy, bookId);
  }
}
