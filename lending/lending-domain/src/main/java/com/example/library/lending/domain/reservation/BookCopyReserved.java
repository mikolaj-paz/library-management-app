package com.example.library.lending.domain.reservation;

import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;

public class BookCopyReserved extends DomainEvent {

  private final ReservationId reservationId;
  private final ReaderId readerId;
  private final BookCopyId bookCopyId;

  public BookCopyReserved(ReservationId reservationId, ReaderId readerId, BookCopyId bookCopyId) {
    super();
    this.reservationId = reservationId;
    this.readerId = readerId;
    this.bookCopyId = bookCopyId;
  }

  @Override
  public String eventName() {
    return "BookCopyReserved";
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
