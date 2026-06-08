package com.example.library.lending.application.command;

import com.example.library.lending.domain.loan.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;

public record ExtendLoanCommand(LoanId loanId, ReaderId readerId) {}
