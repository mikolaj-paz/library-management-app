package com.example.library.lending.infrastructure.out.persistence;

import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.application.query.LoanSummary;
import com.example.library.lending.domain.loan.Loan;
import com.example.library.lending.domain.loan.LoanId;
import com.example.library.lending.domain.loan.LoanStatus;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcLoanRepository implements LoanRepository {

  private final JdbcTemplate jdbc;

  public JdbcLoanRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  private Loan createLoanFromResultSet(ResultSet rs) throws SQLException {
    return Loan.create(
        LoanId.of(rs.getString("id")),
        ReaderId.of(rs.getString("reader_id")),
        BookCopyId.of(rs.getString("book_copy_id")),
        LocalDate.parse(rs.getString("due_date")),
        LoanStatus.valueOf(rs.getString("status")));
  }

  @Override
  public void create(Loan loan) {
    jdbc.update(
        "INSERT INTO loans (id, book_copy_id, reader_id, due_date, status) VALUES (?, ?, ?, ?, ?)",
        loan.id().value().toString(),
        loan.bookCopyId().value().toString(),
        loan.readerId().value().toString(),
        loan.dueDate(),
        loan.status().toString());
  }

  @Override
  public void update(Loan loan) {
    jdbc.update(
        "UPDATE loans SET due_date = ?, status = ? WHERE id = ?",
        loan.dueDate(),
        loan.status().toString(),
        loan.id().value().toString());
  }

  @Override
  public int countActiveLoansForReader(ReaderId readerId) {
    var count =
        jdbc.queryForObject(
            "SELECT COUNT(*) FROM loans WHERE reader_id = ?",
            Integer.class,
            readerId.value().toString());
    return count != null ? count : 0;
  }

  @Override
  public Optional<Loan> findActiveLoan(BookCopyId bookCopyId) {
    var results =
        jdbc.query(
            """
      SELECT id, book_copy_id, reader_id, due_date, status
      FROM loans
      WHERE book_copy_id = ? AND (status = 'ACTIVE' OR status = 'EXTENDED')
      """,
            (rs, rowNum) -> createLoanFromResultSet(rs),
            bookCopyId.value().toString());
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public Optional<Loan> find(LoanId loanId) {
    var results =
        jdbc.query(
            "SELECT id, book_copy_id, reader_id, due_date, status FROM loans WHERE id = ?",
            (rs, rowNum) -> createLoanFromResultSet(rs),
            loanId.value().toString());
    return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
  }

  @Override
  public List<LoanSummary> findLoansFor(ReaderId readerId) {
    return jdbc.query(
        """
      SELECT l.id, l.book_copy_id, b.title, b.author, l.due_date, l.status
      FROM loans l
      LEFT JOIN book_copies bc ON bc.id = l.book_copy_id
      LEFT JOIN books b ON b.id = bc.book_id
      WHERE l.reader_id = ?
      ORDER BY l.due_date DESC
      """,
        (rs, rowNum) ->
            new LoanSummary(
                LoanId.of(rs.getString("id")),
                BookCopyId.of(rs.getString("book_copy_id")),
                rs.getString("title"),
                rs.getString("author"),
                LocalDate.parse(rs.getString("due_date")),
                LoanStatus.valueOf(rs.getString("status"))),
        readerId.value().toString());
  }
}
