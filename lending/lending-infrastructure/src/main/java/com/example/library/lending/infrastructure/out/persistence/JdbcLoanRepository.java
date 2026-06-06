package com.example.library.lending.infrastructure.out.persistence;

import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.domain.loan.Loan;
import com.example.library.sharedkernel.identifier.ReaderId;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcLoanRepository implements LoanRepository {

  private final JdbcTemplate jdbc;

  public JdbcLoanRepository(JdbcTemplate jdbc) {
    this.jdbc = jdbc;
  }

  @Override
  public void create(Loan loan) {
    jdbc.update(
        "INSERT INTO loans (id, book_copy_id, reader_id, due_date) VALUES (?, ?, ?, ?)",
        loan.id().value().toString(),
        loan.bookCopyId().value().toString(),
        loan.readerId().value().toString(),
        loan.dueDate());
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
}
