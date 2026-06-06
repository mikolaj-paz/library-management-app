package com.example.library.lending.domain.exception;

import com.example.library.sharedkernel.identifier.ReaderId;

public class ReaderBlockedException extends RuntimeException {
  public ReaderBlockedException(ReaderId readerId) {
    super("Reader " + readerId.value() + " is blocked.");
  }
}
