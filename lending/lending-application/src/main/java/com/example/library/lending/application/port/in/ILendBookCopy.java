package com.example.library.lending.application.port.in;

import com.example.library.lending.application.command.LendBookCopy;
import com.example.library.lending.domain.loan.LoanId;

public interface ILendBookCopy {

  LoanId execute(LendBookCopy command);
}
