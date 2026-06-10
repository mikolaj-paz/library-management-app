package com.example.library.lending.domain.copy;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;

public interface BookCopyFactory {

  BookCopy reconstitute(BookCopyId id, BookCopyStatus status, ReaderId reservedBy, BookId bookId);
}
