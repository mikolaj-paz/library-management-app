package com.example.library.lending.application.query;

import com.example.library.lending.domain.loan.LoanId;
import com.example.library.lending.domain.loan.LoanStatus;
import com.example.library.sharedkernel.identifier.BookCopyId;
import java.time.LocalDate;

public record LoanSummary(
    LoanId loanId,
    BookCopyId bookCopyId,
    String bookTitle,
    String author,
    LocalDate dueDate,
    LoanStatus status) {}
