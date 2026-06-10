package com.example.library.lending.domain.loan;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDate;

public interface LoanFactory {

  Loan create(ReaderId readerId, BookCopyId bookCopyId);

  Loan reconstitute(
      LoanId id, ReaderId readerId, BookCopyId bookCopyId, LocalDate dueDate, LoanStatus status);
}
