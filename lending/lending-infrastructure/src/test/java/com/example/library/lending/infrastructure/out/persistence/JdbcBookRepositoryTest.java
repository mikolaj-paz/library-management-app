package com.example.library.lending.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.lending.domain.book.BookFactoryImpl;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
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
    repository = new JdbcBookRepository(jdbc, new BookFactoryImpl());
  }

  @Test
  void should_find_book_without_queued_reader() {
    var bookId = BookId.create();
    insertBook(bookId);

    var book = repository.find(bookId);

    assertThat(book).isPresent();
    assertThat(book.get().id()).isEqualTo(bookId);
    assertThat(book.get().waitingQueue()).isEmpty();
    assertThat(book.get().hasQueuedReader()).isFalse();
  }

  @Test
  void should_find_book_with_queued_reader() {
    var bookId = BookId.create();
    var queuedReaderId = ReaderId.create();
    insertBook(bookId);
    insertQueuedReader(bookId, queuedReaderId, 1);

    var book = repository.find(bookId);

    assertThat(book).isPresent();
    assertThat(book.get().waitingQueue()).containsExactly(queuedReaderId);
    assertThat(book.get().hasQueuedReader()).isTrue();
  }

  private void insertBook(BookId bookId) {
    jdbc.update(
        "INSERT INTO books (id, title, author, isbn) VALUES (?, ?, ?, ?)",
        bookId.value().toString(),
        "Domain-Driven Design",
        "Eric Evans",
        bookId.value().toString());
  }

  private void insertQueuedReader(BookId bookId, ReaderId queuedReaderId, int position) {
    jdbc.update(
        "INSERT INTO book_waiting_queue (id, book_id, reader_id, queue_position) VALUES (?, ?, ?, ?)",
        UUID.randomUUID().toString(),
        bookId.value().toString(),
        queuedReaderId.value().toString(),
        position);
  }
}
