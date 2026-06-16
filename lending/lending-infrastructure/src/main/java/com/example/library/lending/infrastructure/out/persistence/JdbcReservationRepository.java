package com.example.library.lending.infrastructure.out.persistence;

import com.example.library.lending.application.port.out.ReservationPersistencePort;
import com.example.library.lending.domain.reservation.Reservation;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcReservationRepository implements ReservationPersistencePort {

  private final JdbcTemplate jdbc;

  public JdbcReservationRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public void create(Reservation reservation) {
    jdbc.update(
        "INSERT INTO reservations (id, reader_id, book_copy_id) VALUES (?, ?, ?)",
        reservation.id().value().toString(),
        reservation.readerId().value().toString(),
        reservation.bookCopyId().value().toString());
  }
}
