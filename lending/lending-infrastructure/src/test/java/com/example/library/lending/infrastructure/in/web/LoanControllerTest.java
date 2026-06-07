package com.example.library.lending.infrastructure.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.lending.application.command.LendBookCopy;
import com.example.library.lending.application.port.in.ILendBookCopy;
import com.example.library.lending.domain.exception.BookCopyNotAvailableException;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.lending.domain.loan.LoanId;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
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

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(new LoanController(lendBookCopyService))
            .setMessageConverters(new MappingJackson2HttpMessageConverter())
            .build();
  }

  @Test
  void should_return_loan_id_when_lending_succeeds() throws Exception {
    var copyId = BookCopyId.create();
    var readerId = ReaderId.create();
    var loanId = LoanId.create();
    when(lendBookCopyService.lend(any())).thenReturn(loanId);

    mockMvc
        .perform(
            post("/loans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(copyId.value().toString(), readerId.value().toString())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.loanId").value(loanId.value().toString()));

    var commandCaptor = ArgumentCaptor.forClass(LendBookCopy.class);
    verify(lendBookCopyService).lend(commandCaptor.capture());
    assertThat(commandCaptor.getValue().bookCopyId()).isEqualTo(copyId);
    assertThat(commandCaptor.getValue().readerId()).isEqualTo(readerId);
  }

  @Test
  void should_return_unprocessable_entity_when_reader_is_blocked() throws Exception {
    var copyId = BookCopyId.create();
    var readerId = ReaderId.create();
    when(lendBookCopyService.lend(any())).thenThrow(new ReaderBlockedException(readerId));

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
    when(lendBookCopyService.lend(any())).thenThrow(new LoanLimitExceededException(readerId));

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
    when(lendBookCopyService.lend(any())).thenThrow(new BookCopyNotAvailableException(copyId));

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
}
