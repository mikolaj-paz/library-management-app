package com.example.library.lending.domain.reservation;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.identifier.ReservationId;
import java.time.LocalDateTime;

public interface ReservationFactory {

  Reservation create(ReaderId readerId, BookCopyId bookCopyId);

  Reservation reconstitute(
      ReservationId id, ReaderId readerId, BookCopyId bookCopyId, LocalDateTime expiresAt);
}
