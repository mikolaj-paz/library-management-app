package com.example.library.lending.infrastructure.out.persistence;

import com.example.library.lending.application.port.out.ReaderPersistencePort;
import com.example.library.lending.domain.reader.Reader;
import com.example.library.lending.domain.reader.ReaderFactory;
import com.example.library.lending.domain.reader.ReaderStatus;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcReaderRepository implements ReaderPersistencePort {

  private final JdbcTemplate jdbc;
  private final ReaderFactory readerFactory;

  public JdbcReaderRepository(JdbcTemplate jdbc, ReaderFactory readerFactory) {
    this.jdbc = jdbc;
    this.readerFactory = readerFactory;
  }

  @Override
  public Optional<Reader> find(ReaderId id) {
    var results =
        jdbc.query(
            "SELECT id, status FROM readers WHERE id = ?",
            (rs, rowNum) ->
                readerFactory.reconstitute(
                    ReaderId.of(rs.getString("id")), ReaderStatus.valueOf(rs.getString("status"))),
            id.value().toString());
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }
}
