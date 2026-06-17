package com.example.library.users.infrastructure.out.persistance;

import com.example.library.sharedkernel.identifier.ReaderAccountId;
import com.example.library.users.application.port.out.ReaderAccountPersistancePort;
import com.example.library.users.domain.reader.ReaderAccount;
import com.example.library.users.domain.reader.ReaderAccountFactory;
import com.example.library.users.domain.reader.ReaderAccountStatus;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcReaderAccountRepository implements ReaderAccountPersistancePort {

  private final JdbcTemplate jdbc;
  private final ReaderAccountFactory readerAccountFactory;

  public JdbcReaderAccountRepository(JdbcTemplate jdbc, ReaderAccountFactory readerAccountFactory) {
    this.jdbc = jdbc;
    this.readerAccountFactory = readerAccountFactory;
  }

  private ReaderAccount createReaderAccountFromResultSet(ResultSet rs) throws SQLException {
    return readerAccountFactory.reconstitute(
        ReaderAccountId.of(rs.getString("id")),
        rs.getString("name"),
        rs.getString("surname"),
        rs.getString("email"),
        rs.getString("telephone"),
        rs.getString("password"),
        ReaderAccountStatus.valueOf(rs.getString("status")));
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
            (rs, rowNum) -> createReaderAccountFromResultSet(rs),
            email);
    return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
  }

  @Override
  public Optional<ReaderAccount> find(ReaderAccountId readerAccountId) {
    var result =
        jdbc.query(
            "SELECT * FROM readers WHERE id = ?",
            (rs, rowNum) -> createReaderAccountFromResultSet(rs),
            readerAccountId.value().toString());
    return result.isEmpty() ? Optional.empty() : Optional.of(result.get(0));
  }

  @Override
  public void update(ReaderAccount readerAccount) {
    jdbc.update(
        "UPDATE readers SET name = ?, surname = ?, email = ?, telephone = ?, password = ?, status = ? WHERE id = ?",
        readerAccount.name(),
        readerAccount.surname(),
        readerAccount.email(),
        readerAccount.telephone(),
        readerAccount.password(),
        readerAccount.status().toString(),
        readerAccount.id().value().toString());
  }
}
