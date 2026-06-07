package com.example.library.catalog.infrastructure.out.persistence;

import com.example.library.catalog.application.port.out.BookCopyRepository;
import com.example.library.catalog.domain.copy.BookCopy;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcBookCopyRepository implements BookCopyRepository {

  private final JdbcTemplate jdbc;

  public JdbcBookCopyRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public void create(BookCopy bookCopy) {
    jdbc.update(
        "INSERT INTO book_copies (id, status, reserved_by, book_id) VALUES (?, ?, ?, ?)",
        bookCopy.id().value().toString(),
        bookCopy.status().toString(),
        bookCopy.reservedBy() != null ? bookCopy.reservedBy().value().toString() : null,
        bookCopy.bookId().value().toString());
  }
}
