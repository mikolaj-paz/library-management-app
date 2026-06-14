package com.example.library.lending.domain.book;

import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;

public class Book extends AggregateRoot<BookId> {

  private ReaderId queuedReaderId;

  private Book(BookId id, ReaderId queuedReaderId) {
    super(id);
    this.queuedReaderId = queuedReaderId;
  }

  static Book of(BookId id, ReaderId queuedReaderId) {
    return new Book(id, queuedReaderId);
  }

  public ReaderId queuedReaderId() {
    return queuedReaderId;
  }

  public boolean hasQueuedReader() {
    return queuedReaderId != null;
  }
}
