package com.example.library.lending.domain.copy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.library.lending.domain.exception.BookCopyNotAvailableException;
import com.example.library.sharedkernel.event.BookCopyLoaned;
import com.example.library.sharedkernel.event.BookCopyReserved;
import com.example.library.sharedkernel.event.BookCopyReturned;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.identifier.ReservationId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import org.junit.jupiter.api.Test;

class BookCopyTest {

  private final BookCopyId copyId = BookCopyId.create();
  private final BookId bookId = BookId.create();
  private final ReaderId readerId = ReaderId.create();
  private final BookCopyFactory factory = new BookCopyFactoryImpl();

  @Test
  void should_allow_loan_when_copy_is_available() {
    var copy = factory.reconstitute(copyId, BookCopyStatus.AVAILABLE, null, bookId);

    assertThatCode(() -> copy.lend(readerId, LoanId.create())).doesNotThrowAnyException();
  }

  @Test
  void should_reject_loan_when_copy_is_already_loaned() {
    var copy = factory.reconstitute(copyId, BookCopyStatus.LOANED, null, bookId);

    assertThatThrownBy(() -> copy.lend(readerId, LoanId.create()))
        .isInstanceOf(BookCopyNotAvailableException.class);
  }

  @Test
  void should_allow_loan_when_copy_is_reserved_by_same_reader() {
    var copy = factory.reconstitute(copyId, BookCopyStatus.RESERVED, readerId, bookId);

    assertThatCode(() -> copy.lend(readerId, LoanId.create())).doesNotThrowAnyException();
  }

  @Test
  void should_reject_loan_when_copy_is_reserved_by_another_reader() {
    var copy = factory.reconstitute(copyId, BookCopyStatus.RESERVED, ReaderId.create(), bookId);

    assertThatThrownBy(() -> copy.lend(readerId, LoanId.create()))
        .isInstanceOf(BookCopyNotAvailableException.class);
  }

  @Test
  void should_mark_copy_as_loaned_and_clear_reservation() {
    var copy = factory.reconstitute(copyId, BookCopyStatus.RESERVED, readerId, bookId);
    var loanId = LoanId.create();

    copy.lend(readerId, loanId);

    assertThat(copy.status()).isEqualTo(BookCopyStatus.LOANED);
    assertThat(copy.reservedBy()).isNull();
    assertThat(copy.pullDomainEvents())
        .singleElement()
        .isInstanceOfSatisfying(
            BookCopyLoaned.class,
            event -> {
              assertThat(event.loanId()).isEqualTo(loanId);
              assertThat(event.readerId()).isEqualTo(readerId);
              assertThat(event.bookCopyId()).isEqualTo(copyId);
            });
  }

  @Test
  void should_mark_copy_as_reserved_for_reader() {
    var copy = factory.reconstitute(copyId, BookCopyStatus.AVAILABLE, null, bookId);
    var reservationId = ReservationId.create();

    copy.reserve(reservationId, readerId);

    assertThat(copy.status()).isEqualTo(BookCopyStatus.RESERVED);
    assertThat(copy.reservedBy()).isEqualTo(readerId);
    assertThat(copy.pullDomainEvents())
        .singleElement()
        .isInstanceOfSatisfying(
            BookCopyReserved.class,
            event -> {
              assertThat(event.reservationId()).isEqualTo(reservationId);
              assertThat(event.readerId()).isEqualTo(readerId);
              assertThat(event.bookCopyId()).isEqualTo(copyId);
            });
  }

  @Test
  void should_mark_copy_as_available_when_returned() {
    var copy = factory.reconstitute(copyId, BookCopyStatus.LOANED, null, bookId);

    copy.returnIt(readerId, true);

    assertThat(copy.status()).isEqualTo(BookCopyStatus.AVAILABLE);
    assertThat(copy.pullDomainEvents())
        .singleElement()
        .isInstanceOfSatisfying(
            BookCopyReturned.class,
            event -> {
              assertThat(event.readerId()).isEqualTo(readerId);
              assertThat(event.bookCopyId()).isEqualTo(copyId);
              assertThat(event.isOverdue()).isTrue();
            });
  }
}
