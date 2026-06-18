package com.example.library;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.time.LocalDate;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class LendingAcceptanceTest {

  @Autowired private MockMvc mockMvc;

  @Autowired private JdbcTemplate jdbc;

  @BeforeEach
  void setUp() {
    jdbc.update("DELETE FROM reservations");
    jdbc.update("DELETE FROM loans");
    jdbc.update("DELETE FROM book_copies");
    jdbc.update("DELETE FROM readers");
    jdbc.update("DELETE FROM books");
  }

  @Test
  void should_lend_available_copy_and_persist_loan() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());
    var copyId = BookCopyId.create();
    insertReader(readerId, "ACTIVE");
    insertBook(bookId);
    insertCopy(copyId, bookId, BookCopyStatus.AVAILABLE);

    mockMvc
        .perform(
            post("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loanRequest(copyId, readerId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.loanId", notNullValue()));

    assertThat(countRows("loans")).isEqualTo(1);
    assertThat(copyStatus(copyId)).isEqualTo("LOANED");
  }

  @Test
  void should_return_conflict_when_lending_unavailable_copy() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());
    var copyId = BookCopyId.create();
    insertReader(readerId, "ACTIVE");
    insertBook(bookId);
    insertCopy(copyId, bookId, BookCopyStatus.LOANED);

    mockMvc
        .perform(
            post("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loanRequest(copyId, readerId)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error", notNullValue()));

    assertThat(countRows("loans")).isZero();
  }

  @Test
  void should_return_unprocessable_entity_when_blocked_reader_lends() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());
    var copyId = BookCopyId.create();
    insertReader(readerId, "BLOCKED");
    insertBook(bookId);
    insertCopy(copyId, bookId, BookCopyStatus.AVAILABLE);

    mockMvc
        .perform(
            post("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loanRequest(copyId, readerId)))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error", notNullValue()));
  }

  @Test
  void should_reserve_available_book_copy_and_persist_reservation() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());
    var copyId = BookCopyId.create();
    insertReader(readerId, "ACTIVE");
    insertBook(bookId);
    insertCopy(copyId, bookId, BookCopyStatus.AVAILABLE);

    mockMvc
        .perform(
            post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationRequest(readerId, bookId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reservationId", notNullValue()));

    assertThat(countRows("reservations")).isEqualTo(1);
  }

  @Test
  void should_return_message_when_no_copy_is_available_for_reservation() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());
    var copyId = BookCopyId.create();
    insertReader(readerId, "ACTIVE");
    insertBook(bookId);
    insertCopy(copyId, bookId, BookCopyStatus.LOANED);

    mockMvc
        .perform(
            post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(reservationRequest(readerId, bookId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", notNullValue()));

    assertThat(countRows("reservations")).isZero();
  }

  @Test
  void should_add_book_and_persist_metadata() throws Exception {
    mockMvc
        .perform(post("/books").contentType(MediaType.APPLICATION_JSON).content(addBookRequest()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.bookId", notNullValue()));

    assertThat(countRows("books")).isEqualTo(1);
    assertThat(
            jdbc.queryForObject(
                "SELECT publisher FROM books WHERE isbn = ?", String.class, "978-0321125217"))
        .isEqualTo("Addison-Wesley");
    assertThat(
            jdbc.queryForObject(
                "SELECT publication_date FROM books WHERE isbn = ?",
                String.class,
                "978-0321125217"))
        .isEqualTo("2003-08-30");
  }

  @Test
  void should_remove_available_book_copy_and_mark_it_withdrawn() throws Exception {
    var bookId = BookId.of(UUID.randomUUID().toString());
    var copyId = BookCopyId.create();
    insertBook(bookId);
    insertCopy(copyId, bookId, BookCopyStatus.AVAILABLE);

    mockMvc
        .perform(
            post("/book-copies/remove")
                .contentType(MediaType.APPLICATION_JSON)
                .content(removeCopyRequest(copyId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", notNullValue()));

    assertThat(copyStatus(copyId)).isEqualTo("WITHDRAWN");
  }

  @Test
  void should_return_book_copy_and_close_active_loan() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());
    var copyId = BookCopyId.create();
    var loanId = LoanId.create();
    insertReader(readerId, "ACTIVE");
    insertBook(bookId);
    insertCopy(copyId, bookId, BookCopyStatus.LOANED);
    insertLoan(loanId, readerId, copyId, LocalDate.now().plusDays(1), "ACTIVE");

    mockMvc
        .perform(post("/loans/return/{bookCopyId}", copyId.value().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", notNullValue()));

    assertThat(copyStatus(copyId)).isEqualTo("AVAILABLE");
    assertThat(loanStatus(loanId)).isEqualTo("CLOSED");
  }

  @Test
  void should_extend_loan_and_show_it_on_reader_loan_list() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());
    var copyId = BookCopyId.create();
    var loanId = LoanId.create();
    var dueDate = LocalDate.of(2026, 1, 1);
    insertReader(readerId, "ACTIVE");
    insertBook(bookId);
    insertCopy(copyId, bookId, BookCopyStatus.LOANED);
    insertLoan(loanId, readerId, copyId, dueDate, "ACTIVE");

    mockMvc
        .perform(
            post("/loans/extend/{loanId}", loanId.value().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(extendLoanRequest(readerId)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", notNullValue()));

    assertThat(loanStatus(loanId)).isEqualTo("EXTENDED");
    assertThat(loanDueDate(loanId)).isEqualTo("2026-01-15");

    mockMvc
        .perform(post("/loans/list").param("readerId", readerId.value().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].loanId.value").value(loanId.value().toString()))
        .andExpect(jsonPath("$[0].status").value("EXTENDED"));
  }

  @Test
  void should_register_reader_account_and_persist_reader_data() throws Exception {
    mockMvc
        .perform(
            post("/users/readers/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(readerRegistrationRequest()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.readerAccountId", notNullValue()));

    assertThat(countRows("readers")).isEqualTo(1);
    assertThat(readerStatus("jane.doe@example.com")).isEqualTo("ACTIVE");
    assertThat(readerPassword("jane.doe@example.com")).isNotBlank();
  }

  private void insertReader(ReaderId readerId, String status) {
    jdbc.update(
        "INSERT INTO readers (id, status) VALUES (?, ?)", readerId.value().toString(), status);
  }

  private void insertBook(BookId bookId) {
    jdbc.update(
        "INSERT INTO books (id, title, author, isbn) VALUES (?, ?, ?, ?)",
        bookId.value().toString(),
        "Domain-Driven Design",
        "Eric Evans",
        "isbn-" + bookId.value());
  }

  private void insertCopy(BookCopyId copyId, BookId bookId, BookCopyStatus status) {
    jdbc.update(
        "INSERT INTO book_copies (id, status, book_id) VALUES (?, ?, ?)",
        copyId.value().toString(),
        status.name(),
        bookId.value().toString());
  }

  private void insertLoan(
      LoanId loanId, ReaderId readerId, BookCopyId copyId, LocalDate dueDate, String status) {
    jdbc.update(
        "INSERT INTO loans (id, reader_id, book_copy_id, due_date, status) VALUES (?, ?, ?, ?, ?)",
        loanId.value().toString(),
        readerId.value().toString(),
        copyId.value().toString(),
        dueDate.toString(),
        status);
  }

  private int countRows(String table) {
    var count = jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
    return count != null ? count : 0;
  }

  private String copyStatus(BookCopyId copyId) {
    return jdbc.queryForObject(
        "SELECT status FROM book_copies WHERE id = ?", String.class, copyId.value().toString());
  }

  private String loanStatus(LoanId loanId) {
    return jdbc.queryForObject(
        "SELECT status FROM loans WHERE id = ?", String.class, loanId.value().toString());
  }

  private String loanDueDate(LoanId loanId) {
    return jdbc.queryForObject(
        "SELECT due_date FROM loans WHERE id = ?", String.class, loanId.value().toString());
  }

  private String readerStatus(String email) {
    return jdbc.queryForObject("SELECT status FROM readers WHERE email = ?", String.class, email);
  }

  private String readerPassword(String email) {
    return jdbc.queryForObject("SELECT password FROM readers WHERE email = ?", String.class, email);
  }

  private String loanRequest(BookCopyId copyId, ReaderId readerId) {
    return """
        {
          "bookCopyId": "%s",
          "readerId": "%s",
          "dueDate": "2026-06-21"
        }
        """
        .formatted(copyId.value(), readerId.value());
  }

  private String addBookRequest() {
    return """
        {
          "title": "Domain-Driven Design",
          "author": "Eric Evans",
          "isbn": "978-0321125217",
          "publisher": "Addison-Wesley",
          "publicationDate": "2003-08-30"
        }
        """;
  }

  private String removeCopyRequest(BookCopyId copyId) {
    return """
        {
          "bookCopyId": "%s"
        }
        """
        .formatted(copyId.value());
  }

  private String extendLoanRequest(ReaderId readerId) {
    return """
        {
          "readerId": "%s"
        }
        """
        .formatted(readerId.value());
  }

  private String reservationRequest(ReaderId readerId, BookId bookId) {
    return """
        {
          "readerId": "%s",
          "bookId": "%s"
        }
        """
        .formatted(readerId.value(), bookId.value());
  }

  private String readerRegistrationRequest() {
    return """
        {
          "name": "Jane",
          "surname": "Doe",
          "email": "jane.doe@example.com",
          "telephone": "+48123456789"
        }
        """;
  }
}
