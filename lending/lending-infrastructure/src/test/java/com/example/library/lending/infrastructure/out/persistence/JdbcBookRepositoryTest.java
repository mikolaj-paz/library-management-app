package com.example.library.lending.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.lending.domain.book.BookFactoryImpl;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcBookRepositoryTest {

  private JdbcTemplate jdbc;
  private JdbcBookRepository repository;

  @BeforeEach
  void setUp() {
    jdbc = SqliteTestDatabase.createJdbcTemplate();
    repository = new JdbcBookRepository(jdbc, new BookFactoryImpl());
  }

  @Test
  @Disabled("TODO: JdbcBookRepository currently does not handle null queued_reader_id")
  void should_find_book_without_queued_reader() {
    var bookId = BookId.create();
    insertBook(bookId, null);

    var book = repository.find(bookId);

    assertThat(book).isPresent();
    assertThat(book.get().id()).isEqualTo(bookId);
    assertThat(book.get().queuedReaderId()).isNull();
    assertThat(book.get().hasQueuedReader()).isFalse();
  }

  @Test
  void should_find_book_with_queued_reader() {
    var bookId = BookId.create();
    var queuedReaderId = ReaderId.create();
    insertBook(bookId, queuedReaderId);

    var book = repository.find(bookId);

    assertThat(book).isPresent();
    assertThat(book.get().queuedReaderId()).isEqualTo(queuedReaderId);
    assertThat(book.get().hasQueuedReader()).isTrue();
  }

  private void insertBook(BookId bookId, ReaderId queuedReaderId) {
    jdbc.update(
        "INSERT INTO books (id, title, author, isbn, queued_reader_id) VALUES (?, ?, ?, ?, ?)",
        bookId.value().toString(),
        "Domain-Driven Design",
        "Eric Evans",
        bookId.value().toString(),
        queuedReaderId != null ? queuedReaderId.value().toString() : null);
  }
}
