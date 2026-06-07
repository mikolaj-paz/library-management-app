package com.example.library.catalog.domain.book;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.sharedkernel.identifier.BookId;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookTest {

  @Test
  void should_store_book_metadata_when_book_is_created() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    var isbn = new ISBN("978-0132350884");

    var book = Book.create(bookId, "Clean Code", "Robert C. Martin", isbn);

    assertThat(book.id()).isEqualTo(bookId);
    assertThat(book.title()).isEqualTo("Clean Code");
    assertThat(book.author()).isEqualTo("Robert C. Martin");
    assertThat(book.isbn()).isEqualTo(isbn);
  }
}
