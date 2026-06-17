package com.example.library.lending.domain.reservation;

import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.event.ReservationExpired;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.identifier.ReservationId;
import java.time.LocalDateTime;

public class Reservation extends AggregateRoot<ReservationId> {

  private final ReaderId readerId;
  private final BookCopyId bookCopyId;
  private final LocalDateTime expiresAt;

  private Reservation(
      ReservationId id, ReaderId readerId, BookCopyId bookCopyId, LocalDateTime expiresAt) {
    super(id);
    this.readerId = readerId;
    this.bookCopyId = bookCopyId;
    this.expiresAt = expiresAt;
  }

  public static Reservation of(
      ReservationId id, ReaderId readerId, BookCopyId bookCopyId, LocalDateTime expiresAt) {
    return new Reservation(id, readerId, bookCopyId, expiresAt);
  }

  public ReaderId readerId() {
    return readerId;
  }

  public BookCopyId bookCopyId() {
    return bookCopyId;
  }

  public LocalDateTime expiresAt() {
    return expiresAt;
  }

  public void expire() {
    this.registerEvent(new ReservationExpired(this.id(), this.readerId(), this.bookCopyId()));
  }
}
