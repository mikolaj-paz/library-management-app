package com.example.library.lending.domain.book;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;

public interface BookFactory {

  Book reconstitute(BookId id, ReaderId queuedReaderId);
}
