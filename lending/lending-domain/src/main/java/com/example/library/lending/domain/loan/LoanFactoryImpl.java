package com.example.library.lending.domain.loan;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDate;

public class LoanFactoryImpl implements LoanFactory {

  private static final int LOAN_DURATION_DAYS = 14;

  @Override
  public Loan create(ReaderId readerId, BookCopyId bookCopyId) {
    return Loan.of(
        LoanId.create(),
        readerId,
        bookCopyId,
        LocalDate.now().plusDays(LOAN_DURATION_DAYS),
        LoanStatus.ACTIVE);
  }

  @Override
  public Loan reconstitute(
      LoanId id, ReaderId readerId, BookCopyId bookCopyId, LocalDate dueDate, LoanStatus status) {
    return Loan.of(id, readerId, bookCopyId, dueDate, status);
  }
}
