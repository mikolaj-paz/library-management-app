package com.example.library.sharedkernel.event;

import com.example.library.sharedkernel.identifier.ReaderAccountId;

public class ReaderAccountUnblocked extends DomainEvent {

  private final ReaderAccountId readerAccountId;

  public ReaderAccountUnblocked(ReaderAccountId readerAccountId) {
    super();
    this.readerAccountId = readerAccountId;
  }

  public ReaderAccountId readerAccountId() {
    return readerAccountId;
  }
}
