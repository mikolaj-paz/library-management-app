package com.example.library.lending.domain.factory;

import com.example.library.lending.domain.copy.BookCopy;
import com.example.library.lending.domain.patron.Patron;
import com.example.library.lending.domain.reservation.Reservation;
import com.example.library.lending.domain.reservation.ReservationId;
import com.example.library.lending.domain.reservation.ReservationStatus;
import java.time.LocalDate;

public class ReservationFactory {
  public Reservation place(ReservationId id, BookCopy copy, Patron patron) {
    // This should also register the event in the domain event system
    return new Reservation(id, copy.id(), patron.id(), ReservationStatus.PENDING, LocalDate.now());
  }
}
