package com.example.library.catalog.infrastructure.out.persistence;

import com.example.library.catalog.application.port.out.BookRepository;
import com.example.library.catalog.domain.book.Book;
import com.example.library.catalog.domain.book.ISBN;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcBookRepository implements BookRepository {

  private final JdbcTemplate jdbc;

  public JdbcBookRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Optional<Book> find(BookId bookId) {
    var results =
        jdbc.query(
            "SELECT id, title, author, isbn, queued_reader_id FROM books WHERE id = ?",
            (rs, rowNum) ->
                Book.create(
                    BookId.of(rs.getString("id")),
                    rs.getString("title"),
                    rs.getString("author"),
                    new ISBN(rs.getString("isbn")),
                    rs.getString("queued_reader_id") != null
                        ? ReaderId.of(rs.getString("queued_reader_id"))
                        : null),
            bookId.value().toString());
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }
}
