package com.example.library.catalog.application.service;

import com.example.library.catalog.application.command.AddBook;
import com.example.library.catalog.application.port.in.IAddBook;
import com.example.library.catalog.application.repository.BookRepository;
import com.example.library.catalog.domain.book.Book;
import com.example.library.catalog.domain.book.BookFactory;

public class AddingBook implements IAddBook {

  private final BookRepository bookRepository;
  private final BookFactory bookFactory;

  public AddingBook(BookRepository bookRepository, BookFactory bookFactory) {
    this.bookRepository = bookRepository;
    this.bookFactory = bookFactory;
  }

  @Override
  public Book addBook(AddBook command) {

    var title = command.title();
    var author = command.author();
    var isbn = command.isbn();
    var publisher = command.publisher();
    var publicationDate = command.publicationDate();

    // 2. System weryfikuje w bazie danych, czy książka o podanym numerze ISBN już nie istnieje w
    // katalogu.
    bookRepository
        .findByISBN(isbn)
        .ifPresent(
            book -> {
              throw new IllegalArgumentException("Book with ISBN " + isbn + " already exists.");
            });

    // 3. Tworzenie nowego obiektu Książki w systemie.
    var book = bookFactory.create(title, author, isbn, publisher, publicationDate);

    // 4. Zapisanie danych nowej książki w bazie danych katalogu.
    bookRepository.create(book);

    // 5. Bibliotekarz otrzymuje komunikat potwierdzający pomyślne dodanie  nowej książki.
    return book;
  }
}
