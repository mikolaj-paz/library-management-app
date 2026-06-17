package com.example.library.lending.application.port.in;

import com.example.library.lending.application.command.LendBookCopy;
import com.example.library.sharedkernel.identifier.LoanId;

public interface ILendBookCopy {

  LoanId lendBookCopy(LendBookCopy command);
}
