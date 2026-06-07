package com.example.library.lending.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.lending.domain.copy.BookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
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
        "Clean Code",
        "Robert C. Martin",
        "978-0132350884");
  }

  @Test
  void should_find_book_copy_by_id() {
    var copyId = BookCopyId.create();
    insertCopy(copyId, BookCopyStatus.AVAILABLE, null, bookId);

    var copy = repository.find(copyId);

    assertThat(copy).isPresent();
    assertThat(copy.get().id()).isEqualTo(copyId);
    assertThat(copy.get().status()).isEqualTo(BookCopyStatus.AVAILABLE);
  }

  @Test
  void should_find_available_book_copy_for_book() {
    var loanedCopyId = BookCopyId.create();
    var availableCopyId = BookCopyId.create();
    insertCopy(loanedCopyId, BookCopyStatus.LOANED, null, bookId);
    insertCopy(availableCopyId, BookCopyStatus.AVAILABLE, null, bookId);

    var copy = repository.findAvailableBookCopy(bookId);

    assertThat(copy).isPresent();
    assertThat(copy.get().id()).isEqualTo(availableCopyId);
  }

  @Test
  void should_update_copy_status_and_reserved_reader() {
    var copyId = BookCopyId.create();
    var readerId = ReaderId.create();
    insertCopy(copyId, BookCopyStatus.AVAILABLE, null, bookId);
    var copy = BookCopy.create(copyId, BookCopyStatus.RESERVED, readerId);

    repository.update(copy);

    var row =
        jdbc.queryForMap(
            "SELECT status, reserved_by FROM book_copies WHERE id = ?", copyId.value().toString());
    assertThat(row.get("status")).isEqualTo("RESERVED");
    assertThat(row.get("reserved_by")).isEqualTo(readerId.value().toString());
  }

  private void insertCopy(
      BookCopyId copyId, BookCopyStatus status, ReaderId reservedBy, BookId bookId) {
    jdbc.update(
        "INSERT INTO book_copies (id, status, reserved_by, book_id) VALUES (?, ?, ?, ?)",
        copyId.value().toString(),
        status.name(),
        reservedBy != null ? reservedBy.value().toString() : null,
        bookId.value().toString());
  }
}
