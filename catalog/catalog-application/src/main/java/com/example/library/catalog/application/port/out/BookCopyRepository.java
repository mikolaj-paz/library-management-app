package com.example.library.catalog.application.port.out;

import com.example.library.catalog.domain.copy.BookCopy;

public interface BookCopyRepository {

  void create(BookCopy bookCopy);
}
