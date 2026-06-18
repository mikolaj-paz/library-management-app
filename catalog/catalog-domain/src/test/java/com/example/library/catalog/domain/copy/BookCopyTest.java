package com.example.library.catalog.domain.copy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.library.catalog.domain.exception.BookCopyCantBeRemovedException;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.util.UUID;
import org.junit.jupiter.api.Test;

class BookCopyTest {

  @Test
  void should_create_available_book_copy_for_book() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    var factory = new BookCopyFactoryImpl();

    var copy = factory.create(bookId);

    assertThat(copy.id()).isNotNull();
    assertThat(copy.bookId()).isEqualTo(bookId);
    assertThat(copy.status()).isEqualTo(BookCopyStatus.AVAILABLE);
    assertThat(copy.reservedBy()).isNull();
    assertThat(copy.isAvailable()).isTrue();
  }

  @Test
  void should_register_book_copy_added_event_when_copy_is_created() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    var factory = new BookCopyFactoryImpl();

    var copy = factory.create(bookId);

    assertThat(copy.pullDomainEvents())
        .singleElement()
        .isInstanceOfSatisfying(
            BookCopyAdded.class,
            event -> {
              assertThat(event.bookCopyId()).isEqualTo(copy.id());
              assertThat(event.bookId()).isEqualTo(bookId);
            });
  }

  @Test
  void should_withdraw_available_book_copy() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    var copy =
        new BookCopyFactoryImpl()
            .reconstitute(
                com.example.library.sharedkernel.identifier.BookCopyId.create(),
                BookCopyStatus.AVAILABLE,
                null,
                bookId);

    copy.pullDomainEvents();

    copy.remove();

    assertThat(copy.status()).isEqualTo(BookCopyStatus.WITHDRAWN);
    assertThat(copy.pullDomainEvents())
        .singleElement()
        .isInstanceOfSatisfying(
            BookCopyRemoved.class, event -> assertThat(event.bookCopyId()).isEqualTo(copy.id()));
  }

  @Test
  void should_not_register_book_copy_added_event_when_copy_is_reconstituted() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    var copy =
        new BookCopyFactoryImpl()
            .reconstitute(
                com.example.library.sharedkernel.identifier.BookCopyId.create(),
                BookCopyStatus.AVAILABLE,
                null,
                bookId);

    assertThat(copy.pullDomainEvents()).isEmpty();
  }

  @Test
  void should_not_withdraw_reserved_book_copy() {
    var bookId = BookId.of(UUID.randomUUID().toString());
    var copy =
        new BookCopyFactoryImpl()
            .reconstitute(
                com.example.library.sharedkernel.identifier.BookCopyId.create(),
                BookCopyStatus.RESERVED,
                ReaderId.create(),
                bookId);

    assertThatThrownBy(copy::remove).isInstanceOf(BookCopyCantBeRemovedException.class);
  }
}
