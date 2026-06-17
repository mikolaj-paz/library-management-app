package com.example.library.lending.infrastructure.out.persistence;

import com.example.library.lending.application.port.out.ReservationPersistencePort;
import com.example.library.lending.domain.reservation.Reservation;
import com.example.library.lending.domain.reservation.ReservationFactory;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.identifier.ReservationId;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcReservationRepository implements ReservationPersistencePort {

  private final JdbcTemplate jdbc;
  private final ReservationFactory reservationFactory;

  public JdbcReservationRepository(JdbcTemplate jdbc, ReservationFactory reservationFactory) {
    this.jdbc = jdbc;
    this.reservationFactory = reservationFactory;
  }

  @Override
  public void create(Reservation reservation) {
    jdbc.update(
        "INSERT INTO reservations (id, reader_id, book_copy_id, expires_at) VALUES (?, ?, ?, ?)",
        reservation.id().value().toString(),
        reservation.readerId().value().toString(),
        reservation.bookCopyId().value().toString(),
        reservation.expiresAt().toString());
  }

  @Override
  public List<Reservation> findExpiredReservations() {
    return jdbc.query(
        """
        SELECT id, reader_id, book_copy_id, expires_at
        FROM reservations
        WHERE datetime(expires_at) < datetime('now', 'localtime')
        """,
        (rs, rowNum) ->
            reservationFactory.reconstitute(
                ReservationId.of(rs.getString("id")),
                ReaderId.of(rs.getString("reader_id")),
                BookCopyId.of(rs.getString("book_copy_id")),
                LocalDateTime.parse(rs.getString("expires_at"))));
  }
}
