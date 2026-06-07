package com.example.library.lending.infrastructure.in.web;

import com.example.library.lending.application.command.LendBookCopy;
import com.example.library.lending.application.port.in.ILendBookCopy;
import com.example.library.lending.domain.exception.BookCopyNotAvailableException;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/loans")
public class LoanController {

  private final ILendBookCopy lendBookCopyService;

  public LoanController(ILendBookCopy lendBookCopyService) {
    this.lendBookCopyService = lendBookCopyService;
  }

  @PostMapping
  public ResponseEntity<?> lendBookCopy(@RequestBody LendBookCopyRequest request) {
    try {
      var command =
          new LendBookCopy(BookCopyId.of(request.bookCopyId()), ReaderId.of(request.readerId()));
      var loanId = lendBookCopyService.lend(command);
      return ResponseEntity.ok(Map.of("loanId", loanId.value().toString()));
    } catch (ReaderBlockedException | LoanLimitExceededException e) {
      return ResponseEntity.unprocessableEntity().body(Map.of("error", e.getMessage()));
    } catch (BookCopyNotAvailableException e) {
      return ResponseEntity.status(409).body(Map.of("error", e.getMessage()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
  }

  record LendBookCopyRequest(String bookCopyId, String readerId, String dueDate) {}
}
