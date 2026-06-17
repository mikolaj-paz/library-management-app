package com.example.library.lending.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.lending.application.query.LoanSummary;
import com.example.library.lending.application.query.ShowLoans;
import com.example.library.lending.application.repository.LoanRepository;
import com.example.library.lending.domain.loan.LoanStatus;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ShowLoansServiceTest {

  @Mock private LoanRepository loanRepository;

  @Test
  void should_return_loans_for_reader() {
    var readerId = ReaderId.create();
    var loans =
        List.of(
            new LoanSummary(
                LoanId.create(),
                BookCopyId.create(),
                "Domain-Driven Design",
                "Eric Evans",
                LocalDate.of(2026, 1, 1),
                LoanStatus.ACTIVE));
    when(loanRepository.findFor(readerId)).thenReturn(loans);
    var service = new ShowLoansService(loanRepository);

    var result = service.show(new ShowLoans(readerId));

    assertThat(result).containsExactlyElementsOf(loans);
    verify(loanRepository).findFor(readerId);
  }
}
