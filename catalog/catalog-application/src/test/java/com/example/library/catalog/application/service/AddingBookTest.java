package com.example.library.catalog.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.catalog.application.command.AddBook;
import com.example.library.catalog.application.repository.BookRepository;
import com.example.library.catalog.domain.book.Book;
import com.example.library.catalog.domain.book.BookFactoryImpl;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class AddingBookTest {

  @Mock private BookRepository bookRepository;

  @Test
  void should_create_and_store_book_when_isbn_is_unique() {
    var isbn = new ISBN("978-0321125217");
    var publicationDate = LocalDate.of(2003, 8, 30);
    var command =
        new AddBook("Domain-Driven Design", "Eric Evans", isbn, "Addison-Wesley", publicationDate);
    when(bookRepository.findByISBN(isbn)).thenReturn(Optional.empty());
    var service = new AddingBook(bookRepository, new BookFactoryImpl());

    var book = service.addBook(command);

    assertThat(book.id()).isNotNull();
    assertThat(book.title()).isEqualTo(command.title());
    assertThat(book.author()).isEqualTo(command.author());
    assertThat(book.isbn()).isEqualTo(isbn);
    assertThat(book.publisher()).isEqualTo(command.publisher());
    assertThat(book.publicationDate()).isEqualTo(publicationDate);
    var bookCaptor = ArgumentCaptor.forClass(Book.class);
    verify(bookRepository).create(bookCaptor.capture());
    assertThat(bookCaptor.getValue()).isEqualTo(book);
  }

  @Test
  void should_reject_duplicate_isbn() {
    var isbn = new ISBN("978-0321125217");
    var existingBook =
        new BookFactoryImpl()
            .create(
                "Domain-Driven Design",
                "Eric Evans",
                isbn,
                "Addison-Wesley",
                LocalDate.of(2003, 8, 30));
    when(bookRepository.findByISBN(isbn)).thenReturn(Optional.of(existingBook));
    var service = new AddingBook(bookRepository, new BookFactoryImpl());

    assertThatThrownBy(
            () ->
                service.addBook(
                    new AddBook(
                        "Another Book",
                        "Another Author",
                        isbn,
                        "Other Publisher",
                        LocalDate.of(2024, 1, 1))))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");
    verify(bookRepository, never()).create(any());
  }
}
