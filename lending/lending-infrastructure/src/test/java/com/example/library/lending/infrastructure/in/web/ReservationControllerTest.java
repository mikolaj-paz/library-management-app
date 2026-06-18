package com.example.library.lending.infrastructure.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.library.lending.application.command.JoinWaitingQueue;
import com.example.library.lending.application.command.ReserveBookCopy;
import com.example.library.lending.application.port.in.IJoinWaitingQueue;
import com.example.library.lending.application.port.in.IReserveBookCopy;
import com.example.library.lending.domain.exception.BookAlreadyInReaderWaitingQueueException;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.NoAvailableBookCopyException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.identifier.ReservationId;
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
class ReservationControllerTest {

  @Mock private IReserveBookCopy reserveBookCopyService;

  @Mock private IJoinWaitingQueue joinWaitingQueueService;

  private MockMvc mockMvc;

  @BeforeEach
  void setUp() {
    mockMvc =
        MockMvcBuilders.standaloneSetup(
                new ReservationController(reserveBookCopyService, joinWaitingQueueService))
            .setMessageConverters(new MappingJackson2HttpMessageConverter())
            .build();
  }

  @Test
  void should_return_reservation_id_when_reservation_succeeds() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());
    var reservationId = ReservationId.create();
    when(reserveBookCopyService.reserveBookCopy(any())).thenReturn(reservationId);

    mockMvc
        .perform(
            post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(readerId.value().toString(), bookId.value().toString())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.reservationId").value(reservationId.value().toString()));

    var commandCaptor = ArgumentCaptor.forClass(ReserveBookCopy.class);
    verify(reserveBookCopyService).reserveBookCopy(commandCaptor.capture());
    assertThat(commandCaptor.getValue().readerId()).isEqualTo(readerId);
    assertThat(commandCaptor.getValue().bookId()).isEqualTo(bookId);
  }

  @Test
  void should_return_unprocessable_entity_when_reader_is_blocked() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());
    when(reserveBookCopyService.reserveBookCopy(any()))
        .thenThrow(new ReaderBlockedException(readerId));

    mockMvc
        .perform(
            post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(readerId.value().toString(), bookId.value().toString())))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error", notNullValue()));
  }

  @Test
  void should_return_unprocessable_entity_when_loan_limit_is_exceeded() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());
    when(reserveBookCopyService.reserveBookCopy(any()))
        .thenThrow(new LoanLimitExceededException(readerId));

    mockMvc
        .perform(
            post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(readerId.value().toString(), bookId.value().toString())))
        .andExpect(status().isUnprocessableEntity())
        .andExpect(jsonPath("$.error", notNullValue()));
  }

  @Test
  void should_return_message_when_no_available_copy_exists() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());
    when(reserveBookCopyService.reserveBookCopy(any()))
        .thenThrow(new NoAvailableBookCopyException(bookId));

    mockMvc
        .perform(
            post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(readerId.value().toString(), bookId.value().toString())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message", notNullValue()));
  }

  @Test
  void should_return_bad_request_when_identifier_is_invalid() throws Exception {
    mockMvc
        .perform(
            post("/reservations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson("not-a-uuid", UUID.randomUUID().toString())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", notNullValue()));
  }

  @Test
  void should_return_success_when_reader_joins_waiting_queue() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());

    mockMvc
        .perform(
            post("/reservations/queue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(readerId.value().toString(), bookId.value().toString())))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Successfully added to waiting queue"));

    var commandCaptor = ArgumentCaptor.forClass(JoinWaitingQueue.class);
    verify(joinWaitingQueueService).joinWaitingQueue(commandCaptor.capture());
    assertThat(commandCaptor.getValue().readerId()).isEqualTo(readerId);
    assertThat(commandCaptor.getValue().bookId()).isEqualTo(bookId);
  }

  @Test
  void should_return_bad_request_when_reader_is_already_in_waiting_queue() throws Exception {
    var readerId = ReaderId.create();
    var bookId = BookId.of(UUID.randomUUID().toString());
    doThrow(new BookAlreadyInReaderWaitingQueueException(readerId, bookId))
        .when(joinWaitingQueueService)
        .joinWaitingQueue(any());

    mockMvc
        .perform(
            post("/reservations/queue")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson(readerId.value().toString(), bookId.value().toString())))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.error", notNullValue()));
  }

  private String requestJson(String readerId, String bookId) {
    return """
        {
          "readerId": "%s",
          "bookId": "%s"
        }
        """
        .formatted(readerId, bookId);
  }
}
