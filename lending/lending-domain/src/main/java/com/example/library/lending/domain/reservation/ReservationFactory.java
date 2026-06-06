package com.example.library.lending.domain.reservation;

import com.example.library.lending.domain.copy.BookCopy;
import com.example.library.lending.domain.patron.Patron;
import com.example.library.sharedkernel.identifier.ReservationId;
import java.time.LocalDate;
import java.util.Objects;

public class ReservationFactory {
  public Reservation place(ReservationId id, BookCopy copy, Patron patron) {
    Objects.requireNonNull(id, "Reservation ID must not be null");
    Objects.requireNonNull(copy, "Book copy must not be null");
    Objects.requireNonNull(patron, "Patron must not be null");
    return new Reservation(id, copy.id(), patron.id(), ReservationStatus.PENDING, LocalDate.now());
  }
}
