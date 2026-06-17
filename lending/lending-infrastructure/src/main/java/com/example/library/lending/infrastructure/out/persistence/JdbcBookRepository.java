package com.example.library.lending.infrastructure.out.persistence;

import com.example.library.lending.application.port.out.BookPersistencePort;
import com.example.library.lending.domain.book.Book;
import com.example.library.lending.domain.book.BookFactory;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.Optional;
import java.util.UUID;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcBookRepository implements BookPersistencePort {

  private final JdbcTemplate jdbc;
  private final BookFactory bookFactory;

  public JdbcBookRepository(JdbcTemplate jdbc, BookFactory bookFactory) {
    this.jdbc = jdbc;
    this.bookFactory = bookFactory;
  }

  @Override
  public Optional<Book> find(BookId bookId) {
    var books =
        jdbc.query(
            "SELECT id FROM books WHERE id = ?",
            (rs, rowNum) -> BookId.of(rs.getString("id")),
            bookId.value().toString());

    if (books.isEmpty()) {
      return Optional.empty();
    }

    var waitingQueue =
        jdbc.query(
            "SELECT reader_id FROM book_waiting_queue WHERE book_id = ? ORDER BY queue_position ASC",
            (rs, rowNum) -> ReaderId.of(rs.getString("reader_id")),
            bookId.value().toString());

    return Optional.of(bookFactory.reconstitute(bookId, waitingQueue));
  }

  @Override
  public void update(Book book) {
    var bookId = book.id().value().toString();
    var waitingQueue = book.waitingQueue();

    jdbc.update("DELETE FROM book_waiting_queue WHERE book_id = ?", bookId);

    for (int index = 0; index < waitingQueue.size(); index++) {
      var readerId = waitingQueue.get(index);
      jdbc.update(
          "INSERT INTO book_waiting_queue (id, book_id, reader_id, queue_position) VALUES (?, ?, ?, ?)",
          UUID.randomUUID().toString(),
          bookId,
          readerId.value().toString(),
          index + 1);
    }
  }
}
