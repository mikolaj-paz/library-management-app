package com.example.library.catalog.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.catalog.domain.copy.BookCopyFactoryImpl;
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
    repository = new JdbcBookCopyRepository(jdbc, new BookCopyFactoryImpl());
    bookId = BookId.of(UUID.randomUUID().toString());
    jdbc.update(
        "INSERT INTO books (id, title, author, isbn, publisher, publication_date) VALUES (?, ?, ?, ?, ?, ?)",
        bookId.value().toString(),
        "Patterns of Enterprise Application Architecture",
        "Martin Fowler",
        "978-0321127426",
        "Addison-Wesley",
        "2002-11-05");
  }

  @Test
  void should_insert_book_copy_linked_to_book() {
    var copy = new BookCopyFactoryImpl().create(bookId);

    repository.create(copy);

    var row =
        jdbc.queryForMap(
            "SELECT status, reserved_by, book_id FROM book_copies WHERE id = ?",
            copy.id().value().toString());
    assertThat(row.get("status")).isEqualTo("AVAILABLE");
    assertThat(row.get("reserved_by")).isNull();
    assertThat(row.get("book_id")).isEqualTo(bookId.value().toString());
  }

  @Test
  void should_find_book_copy_by_id() {
    var copy = new BookCopyFactoryImpl().create(bookId);
    repository.create(copy);

    var found = repository.find(copy.id());

    assertThat(found).isPresent();
    assertThat(found.get().id()).isEqualTo(copy.id());
    assertThat(found.get().bookId()).isEqualTo(bookId);
    assertThat(found.get().status()).isEqualTo(copy.status());
  }
}
