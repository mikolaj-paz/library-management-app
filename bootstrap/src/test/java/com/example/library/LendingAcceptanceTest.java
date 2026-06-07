package com.example.library;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
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

  private int countRows(String table) {
    var count = jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Integer.class);
    return count != null ? count : 0;
  }

  private String copyStatus(BookCopyId copyId) {
    return jdbc.queryForObject(
        "SELECT status FROM book_copies WHERE id = ?", String.class, copyId.value().toString());
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

  private String reservationRequest(ReaderId readerId, BookId bookId) {
    return """
        {
          "readerId": "%s",
          "bookId": "%s"
        }
        """
        .formatted(readerId.value(), bookId.value());
  }
}
