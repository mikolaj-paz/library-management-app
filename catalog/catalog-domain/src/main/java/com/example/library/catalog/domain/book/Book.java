package com.example.library.catalog.domain.book;

import com.example.library.sharedkernel.aggregate.AggregateRoot;
import com.example.library.sharedkernel.identifier.BookId;

public class Book extends AggregateRoot<BookId> {
  private String title;
  private String author;
  private ISBN isbn;

  private Book(BookId id, String title, String author, ISBN isbn) {
    super(id);
    this.title = title;
    this.author = author;
    this.isbn = isbn;
  }

  public static Book create(BookId id, String title, String author, ISBN isbn) {
    return new Book(id, title, author, isbn);
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
}
