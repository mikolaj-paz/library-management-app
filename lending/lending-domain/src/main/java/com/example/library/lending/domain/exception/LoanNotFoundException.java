package com.example.library.lending.domain.exception;

import com.example.library.sharedkernel.identifier.BookCopyId;

public class LoanNotFoundException extends RuntimeException {

  public LoanNotFoundException(BookCopyId bookCopyId) {
    super("Active loan not found for book copy: " + bookCopyId);
  }
}
