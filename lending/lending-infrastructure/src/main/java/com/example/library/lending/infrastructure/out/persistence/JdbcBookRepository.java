package com.example.library.lending.infrastructure.out.persistence;

import com.example.library.lending.application.port.out.BookPersistencePort;
import com.example.library.lending.domain.book.Book;
import com.example.library.lending.domain.book.BookFactory;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.Optional;
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
    var result =
        jdbc.query(
            "SELECT queued_reader_id FROM books WHERE id = ?",
            (rs, rowNum) ->
                bookFactory.reconstitute(bookId, ReaderId.of(rs.getString("queued_reader_id"))),
            bookId.value().toString());

    return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
  }
}
