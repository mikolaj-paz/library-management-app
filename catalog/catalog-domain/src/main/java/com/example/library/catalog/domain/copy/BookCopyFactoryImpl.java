package com.example.library.catalog.domain.copy;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;

public class BookCopyFactoryImpl implements BookCopyFactory {

  @Override
  public BookCopy create(BookId bookId) {
    return BookCopy.of(BookCopyId.create(), BookCopyStatus.AVAILABLE, null, bookId);
  }

  @Override
  public BookCopy reconstitute(
      BookCopyId id, BookCopyStatus status, ReaderId reservedBy, BookId bookId) {
    return BookCopy.of(id, status, reservedBy, bookId);
  }
}
