package com.example.library.sharedkernel.event;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;

public class ReaderDequeued extends DomainEvent {

  private final BookId bookId;
  private final ReaderId readerId;

  public ReaderDequeued(BookId bookId, ReaderId readerId) {
    super();
    this.bookId = bookId;
    this.readerId = readerId;
  }

  public BookId getBookId() {
    return bookId;
  }

  public ReaderId getReaderId() {
    return readerId;
  }
}
