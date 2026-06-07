package com.example.library.catalog.domain.copy;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookCopyTest {

  @Test
  void should_create_available_book_copy_for_book() {
    var bookId = BookId.of(UUID.randomUUID().toString());

    var copy = BookCopy.create(bookId);

    assertThat(copy.id()).isNotNull();
    assertThat(copy.bookId()).isEqualTo(bookId);
    assertThat(copy.status()).isEqualTo(BookCopyStatus.AVAILABLE);
    assertThat(copy.reservedBy()).isNull();
    assertThat(copy.isAvailable()).isTrue();
  }
}
