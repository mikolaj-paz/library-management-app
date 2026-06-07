package com.example.library.catalog.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.catalog.domain.copy.BookCopy;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcBookCopyRepositoryTest {

  private JdbcTemplate jdbc;
  private JdbcBookCopyRepository repository;
  private BookId bookId;

  @BeforeEach
  void setUp() {
    jdbc = SqliteTestDatabase.createJdbcTemplate();
    repository = new JdbcBookCopyRepository(jdbc);
    bookId = BookId.of(UUID.randomUUID().toString());
    jdbc.update(
        "INSERT INTO books (id, title, author, isbn) VALUES (?, ?, ?, ?)",
        bookId.value().toString(),
        "Patterns of Enterprise Application Architecture",
        "Martin Fowler",
        "978-0321127426");
  }

  @Test
  void should_insert_book_copy_linked_to_book() {
    var copy = BookCopy.create(bookId);

    repository.create(copy);

    var row =
        jdbc.queryForMap(
            "SELECT status, reserved_by, book_id FROM book_copies WHERE id = ?",
            copy.id().value().toString());
    assertThat(row.get("status")).isEqualTo("AVAILABLE");
    assertThat(row.get("reserved_by")).isNull();
    assertThat(row.get("book_id")).isEqualTo(bookId.value().toString());
  }
}
