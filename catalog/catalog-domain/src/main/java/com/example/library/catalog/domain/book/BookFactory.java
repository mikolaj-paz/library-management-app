package com.example.library.catalog.domain.book;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;

public interface BookFactory {

  Book create(String title, String author, ISBN isbn);

  Book reconstitute(BookId id, String title, String author, ISBN isbn, ReaderId queuedReaderId);
}
