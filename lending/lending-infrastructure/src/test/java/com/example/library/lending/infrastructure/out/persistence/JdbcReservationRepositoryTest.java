package com.example.library.lending.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.lending.domain.reservation.ReservationFactoryImpl;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcReservationRepositoryTest {

  private JdbcTemplate jdbc;
  private JdbcReservationRepository repository;

  @BeforeEach
  void setUp() {
    jdbc = SqliteTestDatabase.createJdbcTemplate();
    repository = new JdbcReservationRepository(jdbc);
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
}
