package com.example.library.lending.domain.loan;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LoanTest {

  @Test
  void should_set_due_date_to_fourteen_days_from_now_when_loan_is_created() {
    var readerId = ReaderId.create();
    var copyId = BookCopyId.create();

    var loan = Loan.create(readerId, copyId);

    assertThat(loan.readerId()).isEqualTo(readerId);
    assertThat(loan.bookCopyId()).isEqualTo(copyId);
    assertThat(loan.dueDate()).isEqualTo(LocalDate.now().plusDays(14));
  }
}
