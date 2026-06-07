package com.example.library.catalog.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcCatalogQueryPortTest {

  private JdbcTemplate jdbc;
  private JdbcCatalogQueryPort queryPort;

  @BeforeEach
  void setUp() {
    jdbc = SqliteTestDatabase.createJdbcTemplate();
    queryPort = new JdbcCatalogQueryPort(jdbc);
  }

  @Test
  void should_search_books_by_title_author_or_isbn() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    insertBook(bookId, "Domain-Driven Design", "Eric Evans", "978-0321125217");
    insertCopy(BookCopyId.create(), bookId, "AVAILABLE");

    var results = queryPort.searchBooks("Evans");

    assertThat(results)
        .singleElement()
        .satisfies(
            result -> {
              assertThat(result.bookId()).isEqualTo(bookId);
              assertThat(result.title()).isEqualTo("Domain-Driven Design");
              assertThat(result.author()).isEqualTo("Eric Evans");
              assertThat(result.isbn().value()).isEqualTo("978-0321125217");
              assertThat(result.hasAvailableCopies()).isTrue();
            });
  }

  @Test
  void should_return_book_details_with_copy_counts() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    insertBook(bookId, "Clean Architecture", "Robert C. Martin", "978-0134494166");
    insertCopy(BookCopyId.create(), bookId, "AVAILABLE");
    insertCopy(BookCopyId.create(), bookId, "LOANED");
    insertCopy(BookCopyId.create(), bookId, "RESERVED");

    var details = queryPort.getBookDetails(bookId);

    assertThat(details).isPresent();
    assertThat(details.get().bookId()).isEqualTo(bookId);
    assertThat(details.get().title()).isEqualTo("Clean Architecture");
    assertThat(details.get().totalCopies()).isEqualTo(3);
    assertThat(details.get().availableCopies()).isEqualTo(1);
  }

  @Test
  void should_return_empty_details_when_book_does_not_exist() {
    assertThat(queryPort.getBookDetails(BookId.of(UUID.randomUUID().toString()))).isEmpty();
  }

  private void insertBook(BookId bookId, String title, String author, String isbn) {
    jdbc.update(
        "INSERT INTO books (id, title, author, isbn) VALUES (?, ?, ?, ?)",
        bookId.value().toString(),
        title,
        author,
        isbn);
  }

  private void insertCopy(BookCopyId copyId, BookId bookId, String status) {
    jdbc.update(
        "INSERT INTO book_copies (id, status, book_id) VALUES (?, ?, ?)",
        copyId.value().toString(),
        status,
        bookId.value().toString());
  }
}
