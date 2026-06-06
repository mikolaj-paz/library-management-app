package com.example.library.lending.domain.reader;

import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.primitives.AggregateRoot;

public class Reader extends AggregateRoot<ReaderId> {

  private final ReaderStatus status;

  private Reader(ReaderId id, ReaderStatus status) {
    super(id);
    this.status = status;
  }

  public static Reader create(ReaderId id, ReaderStatus status) {
    return new Reader(id, status);
  }

  public ReaderStatus status() {
    return status;
  }

  public boolean isBlocked() {
    return status == ReaderStatus.BLOCKED;
  }
}
