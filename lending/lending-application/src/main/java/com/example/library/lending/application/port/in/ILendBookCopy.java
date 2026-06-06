package com.example.library.lending.application.port.in;

import com.example.library.lending.application.command.LendBookCopy;

public interface ILendBookCopy {

  void lendBookCopy(LendBookCopy command);
}
