package com.example.library.domain.factory;

import com.example.library.domain.model.BookCopy;
import com.example.library.domain.model.Patron;
import com.example.library.domain.model.Reservation;
import com.example.library.domain.model.ReservationId;
import com.example.library.domain.model.ReservationStatus;
import java.time.LocalDate;

public class ReservationFactory {
  public Reservation place(ReservationId id, BookCopy copy, Patron patron) {
    // This should also register the event in the domain event system
    return new Reservation(id, copy.id(), patron.id(), ReservationStatus.PENDING, LocalDate.now());
  }
}
