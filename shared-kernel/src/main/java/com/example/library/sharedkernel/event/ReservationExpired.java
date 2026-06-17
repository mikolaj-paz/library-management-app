package com.example.library.sharedkernel.event;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.identifier.ReservationId;

public class ReservationExpired extends DomainEvent {

  private final ReservationId reservationId;
  private final ReaderId readerId;
  private final BookCopyId bookCopyId;

  public ReservationExpired(ReservationId reservationId, ReaderId readerId, BookCopyId bookCopyId) {
    super();
    this.reservationId = reservationId;
    this.readerId = readerId;
    this.bookCopyId = bookCopyId;
  }

  public ReservationId reservationId() {
    return reservationId;
  }

  public ReaderId readerId() {
    return readerId;
  }

  public BookCopyId bookCopyId() {
    return bookCopyId;
  }
}
