package com.example.library.catalog.domain.book;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.sharedkernel.event.BookAdded;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.valueobject.ISBN;
import java.time.LocalDate;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

class BookTest {

  @Test
  void should_store_book_metadata_when_book_is_created() {
    var factory = new BookFactoryImpl();
    var isbn = new ISBN("978-0132350884");
    var publicationDate = LocalDate.of(2008, 8, 1);

    var book =
        factory.create("Clean Code", "Robert C. Martin", isbn, "Prentice Hall", publicationDate);

    assertThat(book.id()).isNotNull();
    assertThat(book.title()).isEqualTo("Clean Code");
    assertThat(book.author()).isEqualTo("Robert C. Martin");
    assertThat(book.isbn()).isEqualTo(isbn);
    assertThat(book.publisher()).isEqualTo("Prentice Hall");
    assertThat(book.publicationDate()).isEqualTo(publicationDate);
  }

  @Test
  void should_register_book_added_event_when_book_is_created() {
    var factory = new BookFactoryImpl();
    var isbn = new ISBN("978-0132350884");
    var publicationDate = LocalDate.of(2008, 8, 1);

    var book =
        factory.create("Clean Code", "Robert C. Martin", isbn, "Prentice Hall", publicationDate);

    assertThat(book.pullDomainEvents())
        .singleElement()
        .isInstanceOfSatisfying(
            BookAdded.class,
            event -> {
              assertThat(event.bookId()).isEqualTo(book.id());
              assertThat(event.title()).isEqualTo("Clean Code");
              assertThat(event.author()).isEqualTo("Robert C. Martin");
              assertThat(event.isbn()).isEqualTo(isbn);
              assertThat(event.publisher()).isEqualTo("Prentice Hall");
              assertThat(event.publicationDate()).isEqualTo(publicationDate);
            });
  }

  @Test
  @Disabled("TODO: Reconstituting a Book currently registers BookAdded")
  void should_not_register_book_added_event_when_book_is_reconstituted() {
    var isbn = new ISBN("978-0132350884");
    var publicationDate = LocalDate.of(2008, 8, 1);

    var book =
        new BookFactoryImpl()
            .reconstitute(
                BookId.create(),
                "Clean Code",
                "Robert C. Martin",
                isbn,
                "Prentice Hall",
                publicationDate);

    assertThat(book.pullDomainEvents()).isEmpty();
  }
}
