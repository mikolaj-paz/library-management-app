package com.example.library.lending.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.lending.domain.loan.Loan;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcLoanRepositoryTest {

  private JdbcTemplate jdbc;
  private JdbcLoanRepository repository;

  @BeforeEach
  void setUp() {
    jdbc = SqliteTestDatabase.createJdbcTemplate();
    repository = new JdbcLoanRepository(jdbc);
  }

  @Test
  void should_insert_loan() {
    var readerId = ReaderId.create();
    var copyId = BookCopyId.create();
    var loan = Loan.create(readerId, copyId);

    repository.create(loan);

    var row =
        jdbc.queryForMap(
            "SELECT reader_id, book_copy_id, due_date FROM loans WHERE id = ?",
            loan.id().value().toString());
    assertThat(row.get("reader_id")).isEqualTo(readerId.value().toString());
    assertThat(row.get("book_copy_id")).isEqualTo(copyId.value().toString());
    assertThat(row.get("due_date").toString()).isEqualTo(loan.dueDate().toString());
  }

  @Test
  void should_count_active_loans_for_reader() {
    var readerId = ReaderId.create();
    var otherReaderId = ReaderId.create();
    insertLoan(readerId);
    insertLoan(readerId);
    insertLoan(otherReaderId);

    assertThat(repository.countActiveLoansForReader(readerId)).isEqualTo(2);
  }

  private void insertLoan(ReaderId readerId) {
    var loan = Loan.create(readerId, BookCopyId.create());
    repository.create(loan);
  }
}
