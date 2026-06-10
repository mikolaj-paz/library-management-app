package com.example.library.lending.domain.reservation;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.identifier.ReservationId;

public class ReservationFactoryImpl implements ReservationFactory {

  @Override
  public Reservation create(ReaderId readerId, BookCopyId bookCopyId) {
    return Reservation.of(ReservationId.create(), readerId, bookCopyId);
  }
}
