package com.example.library.lending.domain.reservation;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.identifier.ReservationId;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;

public class ReservationFactoryImpl implements ReservationFactory {

  private static final TemporalAmount RESERVATION_EXPIRATION_TIME = Duration.ofDays(2);

  @Override
  public Reservation create(ReaderId readerId, BookCopyId bookCopyId) {
    return Reservation.of(
        ReservationId.create(),
        readerId,
        bookCopyId,
        LocalDateTime.now().plus(RESERVATION_EXPIRATION_TIME));
  }

  @Override
  public Reservation reconstitute(
      ReservationId id, ReaderId readerId, BookCopyId bookCopyId, LocalDateTime expiresAt) {
    return Reservation.of(id, readerId, bookCopyId, expiresAt);
  }
}
