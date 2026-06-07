package com.example.library.catalog.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.sharedkernel.identifier.BookId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcBookRepositoryTest {

  private JdbcTemplate jdbc;
  private JdbcBookRepository repository;

  @BeforeEach
  void setUp() {
    jdbc = SqliteTestDatabase.createJdbcTemplate();
    repository = new JdbcBookRepository(jdbc);
  }

  @Test
  void should_find_book_by_id() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    insertBook(bookId, "Refactoring", "Martin Fowler", "978-0201485677");

    var book = repository.find(bookId);

    assertThat(book).isPresent();
    assertThat(book.get().id()).isEqualTo(bookId);
    assertThat(book.get().title()).isEqualTo("Refactoring");
    assertThat(book.get().author()).isEqualTo("Martin Fowler");
    assertThat(book.get().isbn().value()).isEqualTo("978-0201485677");
  }

  @Test
  void should_return_empty_when_book_does_not_exist() {
    assertThat(repository.find(BookId.of(UUID.randomUUID().toString()))).isEmpty();
  }

  private void insertBook(BookId bookId, String title, String author, String isbn) {
    jdbc.update(
        "INSERT INTO books (id, title, author, isbn) VALUES (?, ?, ?, ?)",
        bookId.value().toString(),
        title,
        author,
        isbn);
  }
}
