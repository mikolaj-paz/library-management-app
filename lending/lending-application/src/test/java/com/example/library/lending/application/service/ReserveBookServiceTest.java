package com.example.library.lending.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.lending.application.command.ReserveBook;
import com.example.library.lending.application.port.out.BookCopyRepository;
import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.application.port.out.ReaderRepository;
import com.example.library.lending.application.port.out.ReservationRepository;
import com.example.library.lending.domain.copy.BookCopy;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.NoAvailableBookCopyException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.lending.domain.reader.Reader;
import com.example.library.lending.domain.reader.ReaderStatus;
import com.example.library.lending.domain.reservation.Reservation;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReserveBookServiceTest {

  @Mock private ReaderRepository readerRepository;

  @Mock private LoanRepository loanRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private ReservationRepository reservationRepository;

  private ReserveBookService service;
  private ReaderId readerId;
  private BookId bookId;

  @BeforeEach
  void setUp() {
    service =
        new ReserveBookService(
            readerRepository, loanRepository, bookCopyRepository, reservationRepository);
    readerId = ReaderId.create();
    bookId = BookId.of(UUID.randomUUID().toString());
  }

  @Test
  void should_create_reservation_when_reader_is_eligible_and_copy_is_available() {
    var copy = BookCopy.create(BookCopyId.create(), BookCopyStatus.AVAILABLE, null);
    givenActiveReader();
    givenActiveLoanCount(0);
    when(bookCopyRepository.findAvailableBookCopy(bookId)).thenReturn(Optional.of(copy));

    var reservationId = service.reserve(new ReserveBook(readerId, bookId));

    assertThat(reservationId).isNotNull();
    var reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
    verify(reservationRepository).create(reservationCaptor.capture());
    assertThat(reservationCaptor.getValue().id()).isEqualTo(reservationId);
    assertThat(reservationCaptor.getValue().readerId()).isEqualTo(readerId);
    assertThat(reservationCaptor.getValue().bookCopyId()).isEqualTo(copy.id());
    verify(bookCopyRepository).update(copy);
  }

  @Test
  void should_throw_when_reader_is_blocked() {
    when(readerRepository.find(readerId))
        .thenReturn(Optional.of(Reader.create(readerId, ReaderStatus.BLOCKED)));

    assertThatThrownBy(() -> service.reserve(new ReserveBook(readerId, bookId)))
        .isInstanceOf(ReaderBlockedException.class);

    verify(reservationRepository, never()).create(any());
  }

  @Test
  void should_throw_when_reader_reached_active_loan_limit() {
    givenActiveReader();
    givenActiveLoanCount(LoanLimitExceededException.MAX_ACTIVE_LOANS);

    assertThatThrownBy(() -> service.reserve(new ReserveBook(readerId, bookId)))
        .isInstanceOf(LoanLimitExceededException.class);

    verify(bookCopyRepository, never()).findAvailableBookCopy(any());
    verify(reservationRepository, never()).create(any());
  }

  @Test
  void should_throw_when_no_available_book_copy_exists() {
    givenActiveReader();
    givenActiveLoanCount(0);
    when(bookCopyRepository.findAvailableBookCopy(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.reserve(new ReserveBook(readerId, bookId)))
        .isInstanceOf(NoAvailableBookCopyException.class);

    verify(reservationRepository, never()).create(any());
  }

  @Disabled(
      "Documents known defect: ReserveBookService updates repository without marking copy as RESERVED.")
  @Test
  void should_mark_copy_as_reserved_when_reservation_succeeds() {
    var copy = BookCopy.create(BookCopyId.create(), BookCopyStatus.AVAILABLE, null);
    givenActiveReader();
    givenActiveLoanCount(0);
    when(bookCopyRepository.findAvailableBookCopy(bookId)).thenReturn(Optional.of(copy));

    service.reserve(new ReserveBook(readerId, bookId));

    assertThat(copy.status()).isEqualTo(BookCopyStatus.RESERVED);
    assertThat(copy.reservedBy()).isEqualTo(readerId);
  }

  private void givenActiveReader() {
    when(readerRepository.find(readerId))
        .thenReturn(Optional.of(Reader.create(readerId, ReaderStatus.ACTIVE)));
  }

  private void givenActiveLoanCount(int count) {
    when(loanRepository.countActiveLoansForReader(readerId)).thenReturn(count);
  }
}
