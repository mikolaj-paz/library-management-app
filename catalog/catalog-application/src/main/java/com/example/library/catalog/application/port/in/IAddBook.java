package com.example.library.catalog.application.port.in;

import com.example.library.catalog.application.command.AddBook;
import com.example.library.catalog.domain.book.Book;

public interface IAddBook {

  Book addBook(AddBook command);
}
