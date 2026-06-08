package com.example.library.lending.infrastructure.out.persistence;

import com.example.library.lending.application.port.out.BookRepository;
import com.example.library.sharedkernel.identifier.BookId;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcBookRepository implements BookRepository {

  private final JdbcTemplate jdbc;

  public JdbcBookRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public boolean existsReaderInQueue(BookId bookId) {
    var result =
        jdbc.query(
            "SELECT queued_reader_id FROM books WHERE id = ?",
            (rs, rowNum) -> rs.getString("queued_reader_id"),
            bookId.value().toString());
    return !result.isEmpty() && result.get(0) != null;
  }
}
