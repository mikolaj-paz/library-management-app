package com.example.library.lending.application.port.in;

import com.example.library.lending.application.command.ReturnBookCopy;

public interface IReturnBookCopy {

  void returnCopy(ReturnBookCopy command);
}
