package com.example.library.lending.infrastructure.in.web;

import com.example.library.lending.application.command.ReserveBook;
import com.example.library.lending.application.port.in.IReserveBook;
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

  public ReservationController(IReserveBook reserveBookService) {
    this.reserveBookService = reserveBookService;
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
}
