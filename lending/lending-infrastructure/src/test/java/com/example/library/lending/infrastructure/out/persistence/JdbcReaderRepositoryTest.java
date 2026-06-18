package com.example.library.lending.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;

import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.reader.ReaderFactoryImpl;
import com.example.library.lending.domain.reader.ReaderStatus;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcReaderRepositoryTest {

  private JdbcTemplate jdbc;
  private JdbcReaderRepository repository;

  @BeforeEach
  void setUp() {
    jdbc = SqliteTestDatabase.createJdbcTemplate();
    repository = new JdbcReaderRepository(jdbc, new ReaderFactoryImpl());
  }

  @Test
  void should_find_reader_by_id() {
    var readerId = ReaderId.create();
    jdbc.update(
        "INSERT INTO readers (id, status) VALUES (?, ?)", readerId.value().toString(), "BLOCKED");

    var reader = repository.find(readerId);

    assertThat(reader).isPresent();
    assertThat(reader.get().id()).isEqualTo(readerId);
    assertThat(reader.get().status()).isEqualTo(ReaderStatus.BLOCKED);
  }

  @Test
  void should_return_empty_when_reader_does_not_exist() {
    assertThat(repository.find(ReaderId.create())).isEmpty();
  }

  @Test
  void should_ignore_closed_loans_when_reconstituting_reader_loan_count() {
    var readerId = ReaderId.create();
    jdbc.update(
        "INSERT INTO readers (id, status) VALUES (?, ?)", readerId.value().toString(), "ACTIVE");
    for (int i = 0; i < LoanLimitExceededException.MAX_ACTIVE_LOANS; i++) {
      insertLoan(readerId, "CLOSED");
    }

    var reader = repository.find(readerId);

    assertThat(reader).isPresent();
    assertThatCode(() -> reader.get().verifyLoanEligibility()).doesNotThrowAnyException();
  }

  private void insertLoan(ReaderId readerId, String status) {
    jdbc.update(
        "INSERT INTO loans (id, reader_id, book_copy_id, due_date, status) VALUES (?, ?, ?, ?, ?)",
        UUID.randomUUID().toString(),
        readerId.value().toString(),
        UUID.randomUUID().toString(),
        LocalDate.of(2026, 1, 1).toString(),
        status);
  }
}
