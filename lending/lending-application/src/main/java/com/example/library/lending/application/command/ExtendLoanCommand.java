package com.example.library.lending.application.command;

import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;

public record ExtendLoanCommand(LoanId loanId, ReaderId readerId) {}
