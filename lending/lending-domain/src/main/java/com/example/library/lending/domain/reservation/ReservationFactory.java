package com.example.library.lending.domain.reservation;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;

public interface ReservationFactory {

  Reservation create(ReaderId readerId, BookCopyId bookCopyId);
}
