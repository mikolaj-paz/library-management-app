package com.example.library.catalog.infrastructure.out.persistence;

import com.example.library.catalog.application.port.out.CatalogQueryPort;
import com.example.library.catalog.application.query.BookDetails;
import com.example.library.catalog.application.query.BookSearchResult;
import com.example.library.catalog.domain.book.ISBN;
import com.example.library.sharedkernel.identifier.BookId;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcCatalogQueryPort implements CatalogQueryPort {

  private final JdbcTemplate jdbc;

  public JdbcCatalogQueryPort(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public List<BookSearchResult> searchBooks(String phrase) {
    String pattern = "%" + phrase + "%";
    return jdbc.query(
        """
            SELECT
                b.id,
                b.title,
                b.author,
                b.isbn,
                COUNT(CASE WHEN bc.status = 'AVAILABLE' THEN 1 END) AS available_copies
            FROM books b
            LEFT JOIN book_copies bc ON bc.book_id = b.id
            WHERE b.title  LIKE ?
               OR b.author LIKE ?
               OR b.isbn   LIKE ?
            GROUP BY b.id, b.title, b.author, b.isbn
            """,
        (rs, rowNum) ->
            new BookSearchResult(
                BookId.of(rs.getString("id")),
                rs.getString("title"),
                rs.getString("author"),
                new ISBN(rs.getString("isbn")),
                rs.getInt("available_copies") > 0),
        pattern,
        pattern,
        pattern);
  }

  @Override
  public Optional<BookDetails> getBookDetails(BookId bookId) {
    var results =
        jdbc.query(
            """
            SELECT
                b.id,
                b.title,
                b.author,
                b.isbn,
                COUNT(bc.id) AS total_copies,
                COUNT(CASE WHEN bc.status = 'AVAILABLE' THEN 1 END) AS available_copies
            FROM books b
            LEFT JOIN book_copies bc ON bc.book_id = b.id
            WHERE b.id = ?
            GROUP BY b.id, b.title, b.author, b.isbn
            """,
            (rs, rowNum) ->
                new BookDetails(
                    BookId.of(rs.getString("id")),
                    rs.getString("title"),
                    rs.getString("author"),
                    new ISBN(rs.getString("isbn")),
                    rs.getInt("total_copies"),
                    rs.getInt("available_copies")),
            bookId.value().toString());
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }
}
