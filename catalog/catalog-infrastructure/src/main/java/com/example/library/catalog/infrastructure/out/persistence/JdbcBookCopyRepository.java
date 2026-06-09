package com.example.library.catalog.infrastructure.out.persistence;

import com.example.library.catalog.application.port.out.BookCopyRepository;
import com.example.library.catalog.domain.copy.BookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.util.Optional;
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

  @Override
  public Optional<BookCopy> find(BookCopyId bookCopyId) {
    var results =
        jdbc.query(
            "SELECT id, status, reserved_by, book_id FROM book_copies WHERE id = ?",
            (rs, rowNum) ->
                BookCopy.create(
                    BookCopyId.of(rs.getString("id")),
                    BookCopyStatus.valueOf(rs.getString("status")),
                    rs.getString("reserved_by") != null
                        ? ReaderId.of(rs.getString("reserved_by"))
                        : null,
                    BookId.of(rs.getString("book_id"))),
            bookCopyId.value().toString());

    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public void update(BookCopy bookCopy) {
    jdbc.update(
        "UPDATE book_copies SET status = ?, reserved_by = ? WHERE id = ?",
        bookCopy.status().toString(),
        bookCopy.reservedBy() != null ? bookCopy.reservedBy().value().toString() : null,
        bookCopy.id().value().toString());
  }
}
