package com.example.library.domain.model;

import com.example.library.shared.Entity;

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
