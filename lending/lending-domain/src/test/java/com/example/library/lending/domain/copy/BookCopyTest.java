package com.example.library.lending.domain.copy;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.library.lending.domain.exception.BookCopyNotAvailableException;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import org.junit.jupiter.api.Test;

class BookCopyTest {

  private final BookCopyId copyId = BookCopyId.create();
  private final ReaderId readerId = ReaderId.create();

  @Test
  void should_allow_loan_when_copy_is_available() {
    var copy = BookCopy.create(copyId, BookCopyStatus.AVAILABLE, null);

    assertThatCode(() -> copy.verifyCanBeLoanedBy(readerId)).doesNotThrowAnyException();
  }

  @Test
  void should_reject_loan_when_copy_is_already_loaned() {
    var copy = BookCopy.create(copyId, BookCopyStatus.LOANED, null);

    assertThatThrownBy(() -> copy.verifyCanBeLoanedBy(readerId))
        .isInstanceOf(BookCopyNotAvailableException.class);
  }

  @Test
  void should_allow_loan_when_copy_is_reserved_by_same_reader() {
    var copy = BookCopy.create(copyId, BookCopyStatus.RESERVED, readerId);

    assertThatCode(() -> copy.verifyCanBeLoanedBy(readerId)).doesNotThrowAnyException();
  }

  @Test
  void should_reject_loan_when_copy_is_reserved_by_another_reader() {
    var copy = BookCopy.create(copyId, BookCopyStatus.RESERVED, ReaderId.create());

    assertThatThrownBy(() -> copy.verifyCanBeLoanedBy(readerId))
        .isInstanceOf(BookCopyNotAvailableException.class);
  }

  @Test
  void should_mark_copy_as_loaned_and_clear_reservation() {
    var copy = BookCopy.create(copyId, BookCopyStatus.RESERVED, readerId);

    copy.updateStatusAsLoaned();

    assertThat(copy.status()).isEqualTo(BookCopyStatus.LOANED);
    assertThat(copy.reservedBy()).isNull();
  }

  @Test
  void should_mark_copy_as_reserved_for_reader() {
    var copy = BookCopy.create(copyId, BookCopyStatus.AVAILABLE, null);

    copy.updateStatusAsReserved(readerId);

    assertThat(copy.status()).isEqualTo(BookCopyStatus.RESERVED);
    assertThat(copy.reservedBy()).isEqualTo(readerId);
  }
}
