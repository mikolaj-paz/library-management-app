package com.example.library.catalog.infrastructure.out.persistence;

import com.example.library.catalog.application.port.out.BookPersistencePort;
import com.example.library.catalog.domain.book.Book;
import com.example.library.catalog.domain.book.BookFactory;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcBookRepository implements BookPersistencePort {

  private final JdbcTemplate jdbc;
  private final BookFactory factory;

  public JdbcBookRepository(JdbcTemplate jdbc, BookFactory factory) {
    this.jdbc = jdbc;
    this.factory = factory;
  }

  private Book createBookFromResultSet(ResultSet rs) throws SQLException {
    return factory.reconstitute(
        BookId.of(rs.getString("id")),
        rs.getString("title"),
        rs.getString("author"),
        new ISBN(rs.getString("isbn")),
        rs.getString("publisher"),
        LocalDate.parse(rs.getString("publication_date")));
  }

  @Override
  public void create(Book book) {
    jdbc.update(
        "INSERT INTO books (id, title, author, isbn, publisher, publication_date) VALUES (?, ?, ?, ?, ?, ?)",
        book.id().value().toString(),
        book.title(),
        book.author(),
        book.isbn().value(),
        book.publisher(),
        book.publicationDate().toString());
  }

  @Override
  public Optional<Book> find(BookId bookId) {
    var results =
        jdbc.query(
            "SELECT * FROM books WHERE id = ?",
            (rs, rowNum) -> createBookFromResultSet(rs),
            bookId.value().toString());
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public Optional<Book> findByISBN(ISBN isbn) {
    var results =
        jdbc.query(
            "SELECT * FROM books WHERE isbn = ?",
            (rs, rowNum) -> createBookFromResultSet(rs),
            isbn.value());
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }
}
