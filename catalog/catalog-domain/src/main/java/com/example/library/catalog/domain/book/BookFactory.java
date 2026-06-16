package com.example.library.catalog.domain.book;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;

public interface BookFactory {

  Book create(String title, String author, ISBN isbn, String publisher, LocalDate publicationDate);

  Book reconstitute(
      BookId id,
      String title,
      String author,
      ISBN isbn,
      String publisher,
      LocalDate publicationDate,
      ReaderId queuedReaderId);
}
