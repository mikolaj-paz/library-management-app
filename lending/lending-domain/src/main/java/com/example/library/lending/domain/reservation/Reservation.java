package com.example.library.lending.domain.reservation;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.PatronId;
import com.example.library.sharedkernel.identifier.ReservationId;
import com.example.library.sharedkernel.primitives.AggregateRoot;
import java.time.LocalDate;

public class Reservation extends AggregateRoot<ReservationId> {

  private final BookCopyId copyId;
  private final PatronId patronId;
  private ReservationStatus status;
  private LocalDate reservedOn;

  public Reservation(
      ReservationId id,
      BookCopyId copyId,
      PatronId patronId,
      ReservationStatus status,
      LocalDate reservedOn) {
    super(id);
    this.copyId = copyId;
    this.patronId = patronId;
    this.status = status;
    this.reservedOn = reservedOn;
  }
}
