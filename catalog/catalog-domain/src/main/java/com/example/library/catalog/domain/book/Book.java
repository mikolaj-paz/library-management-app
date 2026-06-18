package com.example.library.catalog.domain.book;

import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.event.BookAdded;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;

public class Book extends AggregateRoot<BookId> {
  private final String title;
  private final String author;
  private final ISBN isbn;
  private final String publisher;
  private final LocalDate publicationDate;

  private Book(
      BookId id,
      String title,
      String author,
      ISBN isbn,
      String publisher,
      LocalDate publicationDate) {
    super(id);
    this.title = title;
    this.author = author;
    this.isbn = isbn;
    this.publisher = publisher;
    this.publicationDate = publicationDate;
  }

  static Book create(
      BookId id,
      String title,
      String author,
      ISBN isbn,
      String publisher,
      LocalDate publicationDate) {
    var book = new Book(id, title, author, isbn, publisher, publicationDate);
    book.registerEvent(
        new BookAdded(
            book.id(),
            book.title(),
            book.author(),
            book.isbn(),
            book.publisher(),
            book.publicationDate()));
    return book;
  }

  static Book reconstitute(
      BookId id,
      String title,
      String author,
      ISBN isbn,
      String publisher,
      LocalDate publicationDate) {
    return new Book(id, title, author, isbn, publisher, publicationDate);
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
