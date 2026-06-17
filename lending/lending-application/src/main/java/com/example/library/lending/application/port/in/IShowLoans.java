package com.example.library.lending.application.port.in;

import com.example.library.lending.application.query.LoanSummary;
import com.example.library.lending.application.query.ShowLoans;
import java.util.List;

public interface IShowLoans {

  List<LoanSummary> showLoans(ShowLoans query);
}
