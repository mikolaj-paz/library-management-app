package com.example.library.catalog.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.catalog.domain.book.BookFactoryImpl;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;
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
  void should_find_book_by_id() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    insertBook(bookId, "Refactoring", "Martin Fowler", "978-0201485677");

    var book = repository.find(bookId);

    assertThat(book).isPresent();
    assertThat(book.get().id()).isEqualTo(bookId);
    assertThat(book.get().title()).isEqualTo("Refactoring");
    assertThat(book.get().author()).isEqualTo("Martin Fowler");
    assertThat(book.get().isbn().value()).isEqualTo("978-0201485677");
    assertThat(book.get().publisher()).isEqualTo("Addison-Wesley");
    assertThat(book.get().publicationDate()).isEqualTo(LocalDate.of(1999, 7, 8));
  }

  @Test
  void should_return_empty_when_book_does_not_exist() {
    assertThat(repository.find(BookId.of(UUID.randomUUID().toString()))).isEmpty();
  }

  @Test
  void should_insert_book() {
    var book =
        new BookFactoryImpl()
            .create(
                "Domain-Driven Design",
                "Eric Evans",
                new ISBN("978-0321125217"),
                "Addison-Wesley",
                LocalDate.of(2003, 8, 30));

    repository.create(book);

    var row = jdbc.queryForMap("SELECT * FROM books WHERE id = ?", book.id().value().toString());
    assertThat(row.get("title")).isEqualTo("Domain-Driven Design");
    assertThat(row.get("author")).isEqualTo("Eric Evans");
    assertThat(row.get("isbn")).isEqualTo("978-0321125217");
    assertThat(row.get("publisher")).isEqualTo("Addison-Wesley");
    assertThat(row.get("publication_date")).isEqualTo("2003-08-30");
    assertThat(row.get("queued_reader_id")).isNull();
  }

  @Test
  void should_find_book_by_isbn() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    var isbn = "978-0201485677";
    insertBook(bookId, "Refactoring", "Martin Fowler", isbn);

    var book = repository.findByISBN(new ISBN(isbn));

    assertThat(book).isPresent();
    assertThat(book.get().id()).isEqualTo(bookId);
  }

  private void insertBook(BookId bookId, String title, String author, String isbn) {
    jdbc.update(
        "INSERT INTO books (id, title, author, isbn, publisher, publication_date) VALUES (?, ?, ?, ?, ?, ?)",
        bookId.value().toString(),
        title,
        author,
        isbn,
        "Addison-Wesley",
        "1999-07-08");
  }
}
