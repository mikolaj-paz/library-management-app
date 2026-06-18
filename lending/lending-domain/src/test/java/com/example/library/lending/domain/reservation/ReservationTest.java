package com.example.library.lending.domain.reservation;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import org.junit.jupiter.api.Test;

class ReservationTest {

  @Test
  void should_create_reservation_for_reader_and_book_copy() {
    var readerId = ReaderId.create();
    var copyId = BookCopyId.create();

    var reservation = new ReservationFactoryImpl().create(readerId, copyId);

    assertThat(reservation.id()).isNotNull();
    assertThat(reservation.readerId()).isEqualTo(readerId);
    assertThat(reservation.bookCopyId()).isEqualTo(copyId);
  }
}
