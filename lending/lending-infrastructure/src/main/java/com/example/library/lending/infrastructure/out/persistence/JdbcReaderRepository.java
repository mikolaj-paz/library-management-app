package com.example.library.lending.infrastructure.out.persistence;

import com.example.library.lending.application.port.out.ReaderRepository;
import com.example.library.lending.domain.reader.Reader;
import com.example.library.lending.domain.reader.ReaderStatus;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcReaderRepository implements ReaderRepository {

  private final JdbcTemplate jdbc;

  public JdbcReaderRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public Optional<Reader> findById(ReaderId id) {
    var results =
        jdbc.query(
            "SELECT id, status FROM readers WHERE id = ?",
            (rs, rowNum) ->
                Reader.create(
                    ReaderId.of(rs.getString("id")), ReaderStatus.valueOf(rs.getString("status"))),
            id.value().toString());
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }
}
