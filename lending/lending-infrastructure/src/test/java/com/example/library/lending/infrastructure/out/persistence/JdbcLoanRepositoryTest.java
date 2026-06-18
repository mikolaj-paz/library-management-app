package com.example.library.lending.infrastructure.out.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.lending.domain.loan.LoanFactoryImpl;
import com.example.library.lending.domain.loan.LoanStatus;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;

class JdbcLoanRepositoryTest {

  private JdbcTemplate jdbc;
  private JdbcLoanRepository repository;

  @BeforeEach
  void setUp() {
    jdbc = SqliteTestDatabase.createJdbcTemplate();
    repository = new JdbcLoanRepository(jdbc, new LoanFactoryImpl());
  }

  @Test
  void should_insert_loan() {
    var readerId = ReaderId.create();
    var copyId = BookCopyId.create();
    var loan = new LoanFactoryImpl().create(readerId, copyId);

    repository.create(loan);

    var row =
        jdbc.queryForMap(
            "SELECT reader_id, book_copy_id, due_date, status FROM loans WHERE id = ?",
            loan.id().value().toString());
    assertThat(row.get("reader_id")).isEqualTo(readerId.value().toString());
    assertThat(row.get("book_copy_id")).isEqualTo(copyId.value().toString());
    assertThat(row.get("due_date").toString()).isEqualTo(loan.dueDate().toString());
    assertThat(row.get("status")).isEqualTo("ACTIVE");
  }

  @Test
  void should_find_loans_for_reader() {
    var readerId = ReaderId.create();
    var otherReaderId = ReaderId.create();
    var bookId = BookId.create();
    var copyId = BookCopyId.create();
    var otherCopyId = BookCopyId.create();
    insertBook(bookId);
    insertBookCopy(copyId, bookId);
    insertBookCopy(otherCopyId, bookId);
    insertLoan(readerId, copyId, LoanStatus.ACTIVE);
    insertLoan(otherReaderId, otherCopyId, LoanStatus.ACTIVE);

    var loans = repository.findFor(readerId);

    assertThat(loans).hasSize(1);
    assertThat(loans.get(0).bookCopyId()).isEqualTo(copyId);
    assertThat(loans.get(0).bookTitle()).isEqualTo("Domain-Driven Design");
    assertThat(loans.get(0).author()).isEqualTo("Eric Evans");
    assertThat(loans.get(0).status()).isEqualTo(LoanStatus.ACTIVE);
  }

  private void insertBook(BookId bookId) {
    jdbc.update(
        "INSERT INTO books (id, title, author, isbn) VALUES (?, ?, ?, ?)",
        bookId.value().toString(),
        "Domain-Driven Design",
        "Eric Evans",
        bookId.value().toString());
  }

  private void insertBookCopy(BookCopyId copyId, BookId bookId) {
    jdbc.update(
        "INSERT INTO book_copies (id, status, reserved_by, book_id) VALUES (?, ?, ?, ?)",
        copyId.value().toString(),
        "AVAILABLE",
        null,
        bookId.value().toString());
  }

  private void insertLoan(ReaderId readerId, BookCopyId copyId, LoanStatus status) {
    var loan =
        new LoanFactoryImpl()
            .reconstitute(LoanId.create(), readerId, copyId, LocalDate.of(2026, 1, 1), status);
    repository.create(loan);
  }
}
