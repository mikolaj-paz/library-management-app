package com.example.library.lending.domain.reader;

import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.identifier.ReaderId;

public class Reader extends AggregateRoot<ReaderId> {

  private final ReaderStatus status;

  private Reader(ReaderId id, ReaderStatus status) {
    super(id);
    this.status = status;
  }

  static Reader of(ReaderId id, ReaderStatus status) {
    return new Reader(id, status);
  }

  public ReaderStatus status() {
    return status;
  }

  public boolean isBlocked() {
    return status == ReaderStatus.BLOCKED;
  }
}
