package com.example.library.lending.application.port.in;

import com.example.library.lending.application.command.ExtendLoanCommand;

public interface IExtendLoan {

  void extend(ExtendLoanCommand command);
}
