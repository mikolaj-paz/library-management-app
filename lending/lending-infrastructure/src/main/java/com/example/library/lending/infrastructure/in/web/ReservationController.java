package com.example.library.lending.infrastructure.in.web;

import com.example.library.lending.application.command.JoinWaitingQueue;
import com.example.library.lending.application.command.ReserveBook;
import com.example.library.lending.application.port.in.IJoinWaitingQueue;
import com.example.library.lending.application.port.in.IReserveBook;
import com.example.library.lending.domain.exception.BookAlreadyInReaderWaitingQueueException;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.NoAvailableBookCopyException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reservations")
public class ReservationController {

  private final IReserveBook reserveBookService;
  private final IJoinWaitingQueue joinWaitingQueueService;

  public ReservationController(
      IReserveBook reserveBookService, IJoinWaitingQueue joinWaitingQueueService) {
    this.reserveBookService = reserveBookService;
    this.joinWaitingQueueService = joinWaitingQueueService;
  }

  @PostMapping
  public ResponseEntity<?> reserveBook(@RequestBody ReserveBookRequest request) {
    try {
      var readerId = ReaderId.of(request.readerId());
      var bookId = BookId.of(request.bookId());
      var result = reserveBookService.reserveBook(new ReserveBook(readerId, bookId));

      return ResponseEntity.ok(Map.of("reservationId", result.value().toString()));
    } catch (ReaderBlockedException | LoanLimitExceededException e) {
      return ResponseEntity.unprocessableEntity().body(Map.of("error", e.getMessage()));
    } catch (NoAvailableBookCopyException e) {
      return ResponseEntity.ok(Map.of("message", e.getMessage()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  record ReserveBookRequest(String readerId, String bookId) {}

  @PostMapping("/queue")
  public ResponseEntity<?> joinWaitingQueue(@RequestBody JoinWaitingQueueRequest request) {
    try {
      var readerId = ReaderId.of(request.readerId());
      var bookId = BookId.of(request.bookId());
      joinWaitingQueueService.joinWaitingQueue(new JoinWaitingQueue(readerId, bookId));
      return ResponseEntity.ok(Map.of("message", "Successfully added to waiting queue"));
    } catch (BookAlreadyInReaderWaitingQueueException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  record JoinWaitingQueueRequest(String readerId, String bookId) {}
}
