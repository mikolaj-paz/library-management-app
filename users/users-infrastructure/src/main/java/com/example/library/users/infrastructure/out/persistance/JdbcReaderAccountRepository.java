package com.example.library.users.infrastructure.out.persistance;

import com.example.library.sharedkernel.identifier.ReaderAccountId;
import com.example.library.users.application.port.out.ReaderAccountPersistancePort;
import com.example.library.users.domain.reader.ReaderAccount;
import com.example.library.users.domain.reader.ReaderAccountFactory;
import com.example.library.users.domain.reader.ReaderAccountStatus;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcReaderAccountRepository implements ReaderAccountPersistancePort {

  private final JdbcTemplate jdbc;
  private final ReaderAccountFactory readerAccountFactory;

  public JdbcReaderAccountRepository(JdbcTemplate jdbc, ReaderAccountFactory readerAccountFactory) {
    this.jdbc = jdbc;
    this.readerAccountFactory = readerAccountFactory;
  }

  @Override
  public void create(ReaderAccount readerAccount) {
    jdbc.update(
        "INSERT INTO readers (id, name, surname, email, telephone, password, status) VALUES (?, ?, ?, ?, ?, ?, ?)",
        readerAccount.id().value().toString(),
        readerAccount.name(),
        readerAccount.surname(),
        readerAccount.email(),
        readerAccount.telephone(),
        readerAccount.password(),
        readerAccount.status().toString());
  }

  @Override
  public Optional<ReaderAccount> findByEmail(String email) {
    var result =
        jdbc.query(
            "SELECT * FROM readers WHERE email = ?",
            (rs, rowNum) -> {
              return readerAccountFactory.reconstitute(
                  ReaderAccountId.of(rs.getString("id")),
                  rs.getString("name"),
                  rs.getString("surname"),
                  rs.getString("email"),
                  rs.getString("telephone"),
                  rs.getString("password"),
                  ReaderAccountStatus.valueOf(rs.getString("status")));
            },
            email);
    return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
  }
}
