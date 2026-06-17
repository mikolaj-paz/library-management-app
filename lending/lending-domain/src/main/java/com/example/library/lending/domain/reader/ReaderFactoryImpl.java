package com.example.library.lending.domain.reader;

import com.example.library.sharedkernel.identifier.ReaderId;

public class ReaderFactoryImpl implements ReaderFactory {

  @Override
  public Reader reconstitute(ReaderId id, ReaderStatus status, int activeLoansCount) {
    return Reader.of(id, status, activeLoansCount);
  }
}
