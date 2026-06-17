package com.example.library.lending.application.service;

import com.example.library.lending.application.port.in.IShowLoans;
import com.example.library.lending.application.query.LoanSummary;
import com.example.library.lending.application.query.ShowLoans;
import com.example.library.lending.application.repository.LoanRepository;
import java.util.List;

public class ShowingLoans implements IShowLoans {

  private final LoanRepository loanRepository;

  public ShowingLoans(LoanRepository loanRepository) {
    this.loanRepository = loanRepository;
  }

  @Override
  public List<LoanSummary> showLoans(ShowLoans query) {
    var readerId = query.readerId();

    // 2. Z bazy danych pobierana jest lista wszystkich aktywnych oraz archiwalnych wypożyczeń
    // przypisanych do identyfikatora czytelnika.
    return loanRepository.findFor(readerId);
  }
}
