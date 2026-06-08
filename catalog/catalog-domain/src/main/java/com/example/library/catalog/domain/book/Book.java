package com.example.library.catalog.domain.book;

import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;

public class Book extends AggregateRoot<BookId> {
  private final String title;
  private final String author;
  private final ISBN isbn;
  private ReaderId queuedReaderId;

  private Book(BookId id, String title, String author, ISBN isbn, ReaderId queuedReaderId) {
    super(id);
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.queuedReaderId = queuedReaderId;
  }

  public static Book create(BookId id, String title, String author, ISBN isbn) {
    return new Book(id, title, author, isbn, null);
  }

  public static Book create(
      BookId id, String title, String author, ISBN isbn, ReaderId queuedReaderId) {
    return new Book(id, title, author, isbn, queuedReaderId);
  }

  public String title() {
    return title;
  }

  public String author() {
    return author;
  }

  public ISBN isbn() {
    return isbn;
  }

  public ReaderId queuedReaderId() {
    return queuedReaderId;
  }
}
