package com.example.library.domain.model;

import com.example.library.shared.AggregateRoot;

public class BookCopy extends AggregateRoot<CopyId> {

  private CopyStatus status;

  private void changeStatus(CopyStatus newStatus) {
    this.status = newStatus;
  }

  public BookCopy(CopyId id, CopyStatus status) {
    super(id);
    changeStatus(status);
  }
}
