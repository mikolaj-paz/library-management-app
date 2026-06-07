package com.example.library.lending.domain.reservation;

import com.example.library.sharedkernel.aggregate.AggregateRoot;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;

public class Reservation extends AggregateRoot<ReservationId> {

  private final ReaderId readerId;
  private final BookCopyId bookCopyId;

  private Reservation(ReservationId id, ReaderId readerId, BookCopyId bookCopyId) {
    super(id);
    this.readerId = readerId;
    this.bookCopyId = bookCopyId;
  }

  public static Reservation create(ReaderId readerId, BookCopyId bookCopyId) {
    return new Reservation(ReservationId.create(), readerId, bookCopyId);
  }

  public ReaderId readerId() {
    return readerId;
  }

  public BookCopyId bookCopyId() {
    return bookCopyId;
  }
}
