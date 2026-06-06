package com.example.library.lending.infrastructure.in.web;

import com.example.library.lending.application.command.LendBookCopy;
import com.example.library.lending.application.port.in.ILendBookCopy;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.PatronId;
import java.time.LocalDate;
import java.util.Objects;
import java.util.UUID;

public class LoanController {

  private final ILendBookCopy lendBookCopy;

  public LoanController(ILendBookCopy lendBookCopy) {
    this.lendBookCopy = Objects.requireNonNull(lendBookCopy, "Lending use case must not be null");
  }

  public LoanResponse lend(LendBookCopyRequest request) {
    Objects.requireNonNull(request, "Request must not be null");
    try {
      LendBookCopy command =
          new LendBookCopy(
              new BookCopyId(request.copyId()),
              new PatronId(request.patronId()),
              request.startDate(),
              request.dueDate());
      lendBookCopy.lendBookCopy(command);
      return new LoanResponse(201, "Loan created");
    } catch (IllegalArgumentException exception) {
      return new LoanResponse(400, exception.getMessage());
    } catch (IllegalStateException exception) {
      return new LoanResponse(409, exception.getMessage());
    }
  }

  public record LendBookCopyRequest(
      UUID copyId, UUID patronId, LocalDate startDate, LocalDate dueDate) {}

  public record LoanResponse(int statusCode, String message) {}
}
