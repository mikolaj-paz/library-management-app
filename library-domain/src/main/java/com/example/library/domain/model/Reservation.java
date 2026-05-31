package com.example.library.domain.model;

import com.example.library.shared.AggregateRoot;
import java.time.LocalDate;

public class Reservation extends AggregateRoot<ReservationId> {

  private final CopyId copyId;
  private final PatronId patronId;
  private ReservationStatus status;
  private LocalDate reservedOn;

  public Reservation(
      ReservationId id,
      CopyId copyId,
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
