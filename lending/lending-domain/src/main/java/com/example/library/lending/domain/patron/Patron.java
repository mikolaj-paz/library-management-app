package com.example.library.lending.domain.patron;

import com.example.library.sharedkernel.identifier.PatronId;
import com.example.library.sharedkernel.primitives.Entity;

public class Patron extends Entity<PatronId> {

  private int activeLoansCount;

  public Patron(PatronId id, int activeLoansCount) {
    super(id);
    this.activeLoansCount = activeLoansCount;
  }

  public int activeLoansCount() {
    return activeLoansCount;
  }
}
