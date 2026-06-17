package com.example.library.lending.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.lending.application.command.ReserveBook;
import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.lending.application.repository.ReaderRepository;
import com.example.library.lending.application.repository.ReservationRepository;
import com.example.library.lending.domain.copy.BookCopy;
import com.example.library.lending.domain.copy.BookCopyFactory;
import com.example.library.lending.domain.copy.BookCopyFactoryImpl;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.NoAvailableBookCopyException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.lending.domain.reader.ReaderFactoryImpl;
import com.example.library.lending.domain.reader.ReaderStatus;
import com.example.library.lending.domain.reservation.Reservation;
import com.example.library.lending.domain.reservation.ReservationFactoryImpl;
import com.example.library.sharedkernel.event.BookCopyReserved;
import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReserveBookServiceTest {

  @Mock private ReaderRepository readerRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private ReservationRepository reservationRepository;

  @Mock private DomainEventPublisher eventPublisher;

  private ReservingBook service;
  private ReaderId readerId;
  private BookId bookId;
  private BookCopyFactory bookCopyFactory;

  @BeforeEach
  void setUp() {
    service =
        new ReservingBook(
            readerRepository,
            bookCopyRepository,
            new ReservationFactoryImpl(),
            reservationRepository,
            eventPublisher);
    readerId = ReaderId.create();
    bookId = BookId.of(UUID.randomUUID().toString());
    bookCopyFactory = new BookCopyFactoryImpl();
  }

  @Test
  void should_create_reservation_when_reader_is_eligible_and_copy_is_available() {
    var copy = copy(BookCopyStatus.AVAILABLE, null);
    givenActiveReader();
    when(bookCopyRepository.findAvailableBookCopy(bookId)).thenReturn(Optional.of(copy));

    var reservationId = service.reserveBook(new ReserveBook(readerId, bookId));

    assertThat(reservationId).isNotNull();
    var reservationCaptor = ArgumentCaptor.forClass(Reservation.class);
    verify(reservationRepository).create(reservationCaptor.capture());
    assertThat(reservationCaptor.getValue().id()).isEqualTo(reservationId);
    assertThat(reservationCaptor.getValue().readerId()).isEqualTo(readerId);
    assertThat(reservationCaptor.getValue().bookCopyId()).isEqualTo(copy.id());
    verify(bookCopyRepository).update(copy);
    var eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue())
        .isInstanceOfSatisfying(
            BookCopyReserved.class,
            event -> {
              assertThat(event.reservationId()).isEqualTo(reservationId);
              assertThat(event.readerId()).isEqualTo(readerId);
              assertThat(event.bookCopyId()).isEqualTo(copy.id());
            });
  }

  @Test
  void should_throw_when_reader_is_blocked() {
    when(readerRepository.find(readerId))
        .thenReturn(
            Optional.of(new ReaderFactoryImpl().reconstitute(readerId, ReaderStatus.BLOCKED, 0)));

    assertThatThrownBy(() -> service.reserveBook(new ReserveBook(readerId, bookId)))
        .isInstanceOf(ReaderBlockedException.class);

    verify(reservationRepository, never()).create(any());
  }

  @Test
  void should_throw_when_reader_reached_active_loan_limit() {
    givenActiveReaderWithLoanCount(LoanLimitExceededException.MAX_ACTIVE_LOANS);

    assertThatThrownBy(() -> service.reserveBook(new ReserveBook(readerId, bookId)))
        .isInstanceOf(LoanLimitExceededException.class);

    verify(bookCopyRepository, never()).findAvailableBookCopy(any());
    verify(reservationRepository, never()).create(any());
  }

  @Test
  void should_throw_when_no_available_book_copy_exists() {
    givenActiveReader();
    when(bookCopyRepository.findAvailableBookCopy(bookId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.reserveBook(new ReserveBook(readerId, bookId)))
        .isInstanceOf(NoAvailableBookCopyException.class);

    verify(reservationRepository, never()).create(any());
  }

  @Test
  void should_mark_copy_as_reserved_when_reservation_succeeds() {
    var copy = copy(BookCopyStatus.AVAILABLE, null);
    givenActiveReader();
    when(bookCopyRepository.findAvailableBookCopy(bookId)).thenReturn(Optional.of(copy));

    service.reserveBook(new ReserveBook(readerId, bookId));

    assertThat(copy.status()).isEqualTo(BookCopyStatus.RESERVED);
    assertThat(copy.reservedBy()).isEqualTo(readerId);
  }

  private void givenActiveReader() {
    givenActiveReaderWithLoanCount(0);
  }

  private void givenActiveReaderWithLoanCount(int activeLoansCount) {
    when(readerRepository.find(readerId))
        .thenReturn(
            Optional.of(
                new ReaderFactoryImpl()
                    .reconstitute(readerId, ReaderStatus.ACTIVE, activeLoansCount)));
  }

  private BookCopy copy(BookCopyStatus status, ReaderId reservedBy) {
    return bookCopyFactory.reconstitute(BookCopyId.create(), status, reservedBy, bookId);
  }
}
