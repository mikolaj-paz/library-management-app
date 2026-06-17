package com.example.library.catalog.application.port.in;

import com.example.library.catalog.application.command.AddBookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;

public interface IAddBookCopy {
  BookCopyId addBookCopy(AddBookCopy command);
}
