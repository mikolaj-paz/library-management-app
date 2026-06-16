package com.example.library.sharedkernel.event;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;

public class BookAddedEvent extends DomainEvent {

  private final BookId bookId;
  private final String title;
  private final String author;
  private final ISBN isbn;
  private final String publisher;
  private final LocalDate publicationDate;

  public BookAddedEvent(
      BookId bookId,
      String title,
      String author,
      ISBN isbn,
      String publisher,
      LocalDate publicationDate) {
    super();
    this.bookId = bookId;
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.publisher = publisher;
    this.publicationDate = publicationDate;
  }

  public BookId bookId() {
    return bookId;
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

  public String publisher() {
    return publisher;
  }

  public LocalDate publicationDate() {
    return publicationDate;
  }
}
