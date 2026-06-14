package com.example.library.lending.application.service;

import com.example.library.lending.application.port.in.IShowLoans;
import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.application.query.LoanSummary;
import com.example.library.lending.application.query.ShowLoans;
import java.util.List;

public class ShowLoansService implements IShowLoans {

  private final LoanRepository loanRepository;

  public ShowLoansService(LoanRepository loanRepository) {
    this.loanRepository = loanRepository;
  }

  @Override
  public List<LoanSummary> show(ShowLoans query) {
    var readerId = query.readerId();
    return loanRepository.findFor(readerId);
  }
}
