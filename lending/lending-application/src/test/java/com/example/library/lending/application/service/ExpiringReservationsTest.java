package com.example.library.lending.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.lending.application.repository.ReservationRepository;
import com.example.library.lending.domain.reservation.ReservationFactoryImpl;
import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.event.ReservationExpired;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.identifier.ReservationId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExpiringReservationsTest {

  @Mock private ReservationRepository reservationRepository;

  @Mock private DomainEventPublisher eventPublisher;

  @Test
  void should_publish_event_for_each_expired_reservation() {
    var reservationId = ReservationId.create();
    var readerId = ReaderId.create();
    var bookCopyId = BookCopyId.create();
    var reservation =
        new ReservationFactoryImpl()
            .reconstitute(
                reservationId, readerId, bookCopyId, LocalDateTime.of(2026, 1, 1, 12, 0));
    when(reservationRepository.findExpiredReservations()).thenReturn(List.of(reservation));
    var service = new ExpiringReservations(reservationRepository, eventPublisher);

    service.expireReservations();

    var eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue())
        .isInstanceOfSatisfying(
            ReservationExpired.class,
            event -> {
              assertThat(event.reservationId()).isEqualTo(reservationId);
              assertThat(event.readerId()).isEqualTo(readerId);
              assertThat(event.bookCopyId()).isEqualTo(bookCopyId);
            });
  }
}
