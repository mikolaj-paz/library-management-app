package com.example.library.lending.infrastructure.out.persistence;

import com.example.library.lending.application.port.out.BookCopyRepository;
import com.example.library.lending.domain.copy.BookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcBookCopyRepository implements BookCopyRepository {

  private final JdbcTemplate jdbc;

  public JdbcBookCopyRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  private BookCopy createBookCopyFromResultSet(ResultSet rs) throws SQLException {
    return BookCopy.create(
        BookCopyId.of(rs.getString("id")),
        BookCopyStatus.valueOf(rs.getString("status")),
        rs.getString("reserved_by") != null ? ReaderId.of(rs.getString("reserved_by")) : null,
        BookId.of(rs.getString("book_id")));
  }

  @Override
  public Optional<BookCopy> find(BookCopyId id) {
    var results =
        jdbc.query(
            "SELECT id, status, reserved_by, book_id FROM book_copies WHERE id = ?",
            (rs, rowNum) -> createBookCopyFromResultSet(rs),
            id.value().toString());
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public Optional<BookCopy> findAvailableBookCopy(BookId bookId) {
    var results =
        jdbc.query(
            "SELECT id, status, reserved_by, book_id FROM book_copies WHERE book_id = ? AND status = 'AVAILABLE' LIMIT 1",
            (rs, rowNum) -> createBookCopyFromResultSet(rs),
            bookId.value().toString());
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public void update(BookCopy bookCopy) {
    jdbc.update(
        "UPDATE book_copies SET status = ?, reserved_by = ? WHERE id = ?",
        bookCopy.status().name(),
        bookCopy.reservedBy() != null ? bookCopy.reservedBy().value().toString() : null,
        bookCopy.id().value().toString());
  }
}
