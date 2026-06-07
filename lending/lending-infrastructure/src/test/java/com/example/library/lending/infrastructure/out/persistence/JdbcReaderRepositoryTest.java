package com.example.library.lending.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.lending.domain.reader.ReaderStatus;
import com.example.library.sharedkernel.identifier.ReaderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcReaderRepositoryTest {

  private JdbcTemplate jdbc;
  private JdbcReaderRepository repository;

  @BeforeEach
  void setUp() {
    jdbc = SqliteTestDatabase.createJdbcTemplate();
    repository = new JdbcReaderRepository(jdbc);
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
}
