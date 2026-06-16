package com.example.library.catalog.domain.book;

import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.event.BookAddedEvent;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;

public class Book extends AggregateRoot<BookId> {
  private final String title;
  private final String author;
  private final ISBN isbn;
  private final String publisher;
  private final LocalDate publicationDate;
  private ReaderId queuedReaderId;

  private Book(
      BookId id,
      String title,
      String author,
      ISBN isbn,
      String publisher,
      LocalDate publicationDate,
      ReaderId queuedReaderId) {
    super(id);
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.publisher = publisher;
    this.publicationDate = publicationDate;
    this.queuedReaderId = queuedReaderId;
    registerEvent(new BookAddedEvent(id, title, author, isbn, publisher, publicationDate));
  }

  static Book of(
      BookId id,
      String title,
      String author,
      ISBN isbn,
      String publisher,
      LocalDate publicationDate,
      ReaderId queuedReaderId) {
    return new Book(id, title, author, isbn, publisher, publicationDate, queuedReaderId);
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

  public String publisher() {
    return publisher;
  }

  public LocalDate publicationDate() {
    return publicationDate;
  }
}
