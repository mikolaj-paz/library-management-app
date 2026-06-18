package com.example.library.lending.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.lending.domain.reservation.ReservationFactoryImpl;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.identifier.ReservationId;
import java.time.LocalDateTime;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcReservationRepositoryTest {

  private JdbcTemplate jdbc;
  private JdbcReservationRepository repository;

  @BeforeEach
  void setUp() {
    jdbc = SqliteTestDatabase.createJdbcTemplate();
    repository = new JdbcReservationRepository(jdbc, new ReservationFactoryImpl());
  }

  @Test
  void should_insert_reservation() {
    var readerId = ReaderId.create();
    var copyId = BookCopyId.create();
    var reservation = new ReservationFactoryImpl().create(readerId, copyId);

    repository.create(reservation);

    var row =
        jdbc.queryForMap(
            "SELECT reader_id, book_copy_id FROM reservations WHERE id = ?",
            reservation.id().value().toString());
    assertThat(row.get("reader_id")).isEqualTo(readerId.value().toString());
    assertThat(row.get("book_copy_id")).isEqualTo(copyId.value().toString());
  }

  @Test
  void should_find_expired_reservations() {
    var expiredReservationId = ReservationId.create();
    var activeReservationId = ReservationId.create();
    var readerId = ReaderId.create();
    var expiredCopyId = BookCopyId.create();
    var activeCopyId = BookCopyId.create();
    insertReservation(
        expiredReservationId, readerId, expiredCopyId, LocalDateTime.now().minusDays(1));
    insertReservation(activeReservationId, readerId, activeCopyId, LocalDateTime.now().plusDays(1));

    var reservations = repository.findExpiredReservations();

    assertThat(reservations).hasSize(1);
    assertThat(reservations.get(0).id()).isEqualTo(expiredReservationId);
    assertThat(reservations.get(0).readerId()).isEqualTo(readerId);
    assertThat(reservations.get(0).bookCopyId()).isEqualTo(expiredCopyId);
  }

  private void insertReservation(
      ReservationId reservationId,
      ReaderId readerId,
      BookCopyId bookCopyId,
      LocalDateTime expiresAt) {
    jdbc.update(
        "INSERT INTO reservations (id, reader_id, book_copy_id, expires_at) VALUES (?, ?, ?, ?)",
        reservationId.value().toString(),
        readerId.value().toString(),
        bookCopyId.value().toString(),
        expiresAt.toString());
  }
}
