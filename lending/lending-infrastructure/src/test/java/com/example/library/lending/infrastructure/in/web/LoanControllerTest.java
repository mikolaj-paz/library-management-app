package com.example.library.lending.infrastructure.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.lending.application.command.ExtendLoanCommand;
import com.example.library.lending.application.command.LendBookCopy;
import com.example.library.lending.application.command.ReturnBookCopy;
import com.example.library.lending.application.port.in.IExtendLoan;
import com.example.library.lending.application.port.in.ILendBookCopy;
import com.example.library.lending.application.port.in.IReturnBookCopy;
import com.example.library.lending.application.port.in.IShowLoans;
import com.example.library.lending.application.query.LoanSummary;
import com.example.library.lending.domain.exception.BookCopyNotAvailableException;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.lending.domain.loan.LoanStatus;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
class LoanControllerTest {

  @Mock private ILendBookCopy lendBookCopyService;

  @Mock private IReturnBookCopy returnBookCopyService;

  @Mock private IExtendLoan extendLoanService;

  @Mock private IShowLoans showLoansService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(
                new LoanController(
                    lendBookCopyService,
                    returnBookCopyService,
                    extendLoanService,
                    showLoansService))
            .setMessageConverters(new MappingJackson2HttpMessageConverter())
            .build();
  }

  @Test
  void should_return_loan_id_when_lending_succeeds() throws Exception {
    var copyId = BookCopyId.create();
    var readerId = ReaderId.create();
    var loanId = LoanId.create();
    when(lendBookCopyService.lendBookCopy(any())).thenReturn(loanId);

    mockMvc
        .perform(
            post("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(copyId.value().toString(), readerId.value().toString())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.loanId").value(loanId.value().toString()));

    var commandCaptor = ArgumentCaptor.forClass(LendBookCopy.class);
    verify(lendBookCopyService).lendBookCopy(commandCaptor.capture());
    assertThat(commandCaptor.getValue().bookCopyId()).isEqualTo(copyId);
    assertThat(commandCaptor.getValue().readerId()).isEqualTo(readerId);
  }

  @Test
  void should_return_unprocessable_entity_when_reader_is_blocked() throws Exception {
    var copyId = BookCopyId.create();
    var readerId = ReaderId.create();
    when(lendBookCopyService.lendBookCopy(any())).thenThrow(new ReaderBlockedException(readerId));

    mockMvc
        .perform(
            post("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(copyId.value().toString(), readerId.value().toString())))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error", notNullValue()));
  }

  @Test
  void should_return_unprocessable_entity_when_loan_limit_is_exceeded() throws Exception {
    var copyId = BookCopyId.create();
    var readerId = ReaderId.create();
    when(lendBookCopyService.lendBookCopy(any()))
        .thenThrow(new LoanLimitExceededException(readerId));

    mockMvc
        .perform(
            post("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(copyId.value().toString(), readerId.value().toString())))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error", notNullValue()));
  }

  @Test
  void should_return_conflict_when_copy_is_not_available() throws Exception {
    var copyId = BookCopyId.create();
    var readerId = ReaderId.create();
    when(lendBookCopyService.lendBookCopy(any()))
        .thenThrow(new BookCopyNotAvailableException(copyId));

    mockMvc
        .perform(
            post("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(copyId.value().toString(), readerId.value().toString())))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error", notNullValue()));
  }

  @Test
  void should_return_bad_request_when_identifier_is_invalid() throws Exception {
    mockMvc
        .perform(
            post("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson("not-a-uuid", UUID.randomUUID().toString())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", notNullValue()));
  }

  @Test
  void should_return_success_when_copy_is_returned() throws Exception {
    var copyId = BookCopyId.create();

    mockMvc
        .perform(post("/loans/return/{bookCopyId}", copyId.value().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Book copy returned successfully"));

    var commandCaptor = ArgumentCaptor.forClass(ReturnBookCopy.class);
    verify(returnBookCopyService).returnBookCopy(commandCaptor.capture());
    assertThat(commandCaptor.getValue().bookCopyId()).isEqualTo(copyId);
  }

  @Test
  void should_return_success_when_loan_is_extended() throws Exception {
    var loanId = LoanId.create();
    var readerId = ReaderId.create();

    mockMvc
        .perform(
            post("/loans/extend/{loanId}", loanId.value().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(extendRequestJson(readerId.value().toString())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Loan extended successfully"));

    var commandCaptor = ArgumentCaptor.forClass(ExtendLoanCommand.class);
    verify(extendLoanService).extendLoan(commandCaptor.capture());
    assertThat(commandCaptor.getValue().loanId()).isEqualTo(loanId);
    assertThat(commandCaptor.getValue().readerId()).isEqualTo(readerId);
  }

  @Test
  void should_return_loans_for_reader() throws Exception {
    var readerId = ReaderId.create();
    var loans =
        List.of(
            new LoanSummary(
                LoanId.create(),
                BookCopyId.create(),
                "Domain-Driven Design",
                "Eric Evans",
                LocalDate.of(2026, 1, 1),
                LoanStatus.ACTIVE));
    when(showLoansService.showLoans(any())).thenReturn(loans);

    mockMvc
        .perform(post("/loans/list").param("readerId", readerId.value().toString()))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].bookTitle").value("Domain-Driven Design"));
  }

  private String requestJson(String copyId, String readerId) {
    return """
        {
          "bookCopyId": "%s",
          "readerId": "%s",
          "dueDate": "2026-06-21"
        }
        """
        .formatted(copyId, readerId);
  }

  private String extendRequestJson(String readerId) {
    return """
        {
          "readerId": "%s"
        }
        """
        .formatted(readerId);
  }
}
