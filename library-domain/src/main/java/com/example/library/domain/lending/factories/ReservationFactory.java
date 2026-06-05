package com.example.library.domain.lending.factories;

import com.example.library.domain.lending.copy.BookCopy;
import com.example.library.domain.lending.reservation.Reservation;
import com.example.library.domain.lending.reservation.ReservationId;
import com.example.library.domain.lending.reservation.ReservationStatus;
import com.example.library.domain.model.Patron;

import java.time.LocalDate;

public class ReservationFactory {
  public Reservation place(ReservationId id, BookCopy copy, Patron patron) {
    // This should also register the event in the domain event system
    return new Reservation(id, copy.id(), patron.id(), ReservationStatus.PENDING, LocalDate.now());
  }
}
