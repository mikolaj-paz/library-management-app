package com.example.library.lending.domain.loan;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.library.lending.domain.exception.ExtensionNotAllowedException;
import com.example.library.sharedkernel.event.LoanExtended;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class LoanTest {

  @Test
  void should_set_due_date_to_fourteen_days_from_now_when_loan_is_created() {
    var readerId = ReaderId.create();
    var copyId = BookCopyId.create();

    var loan = new LoanFactoryImpl().create(readerId, copyId);

    assertThat(loan.readerId()).isEqualTo(readerId);
    assertThat(loan.bookCopyId()).isEqualTo(copyId);
    assertThat(loan.dueDate()).isEqualTo(LocalDate.now().plusDays(14));
    assertThat(loan.status()).isEqualTo(LoanStatus.ACTIVE);
  }

  @Test
  void should_close_active_loan() {
    var loan = new LoanFactoryImpl().create(ReaderId.create(), BookCopyId.create());

    loan.close();

    assertThat(loan.status()).isEqualTo(LoanStatus.CLOSED);
  }

  @Test
  void should_report_overdue_when_date_is_after_due_date() {
    var loan =
        new LoanFactoryImpl()
            .reconstitute(
                LoanId.create(),
                ReaderId.create(),
                BookCopyId.create(),
                LocalDate.of(2026, 1, 1),
                LoanStatus.ACTIVE);

    assertThat(loan.isOverdue(LocalDate.of(2026, 1, 2))).isTrue();
    assertThat(loan.isOverdue(LocalDate.of(2026, 1, 1))).isFalse();
  }

  @Test
  void should_extend_active_loan_once_and_register_event() {
    var readerId = ReaderId.create();
    var copyId = BookCopyId.create();
    var loan =
        new LoanFactoryImpl()
            .reconstitute(
                LoanId.create(), readerId, copyId, LocalDate.of(2026, 1, 1), LoanStatus.ACTIVE);

    loan.extend();

    assertThat(loan.status()).isEqualTo(LoanStatus.EXTENDED);
    assertThat(loan.dueDate()).isEqualTo(LocalDate.of(2026, 1, 15));
    assertThat(loan.pullDomainEvents())
        .singleElement()
        .isInstanceOfSatisfying(
            LoanExtended.class,
            event -> {
              assertThat(event.loanId()).isEqualTo(loan.id());
              assertThat(event.readerId()).isEqualTo(readerId);
              assertThat(event.bookCopyId()).isEqualTo(copyId);
              assertThat(event.newDueDate()).isEqualTo(loan.dueDate());
            });
  }

  @Test
  void should_not_extend_closed_or_already_extended_loan() {
    var factory = new LoanFactoryImpl();
    var closedLoan =
        factory.reconstitute(
            LoanId.create(),
            ReaderId.create(),
            BookCopyId.create(),
            LocalDate.of(2026, 1, 1),
            LoanStatus.CLOSED);
    var extendedLoan =
        factory.reconstitute(
            LoanId.create(),
            ReaderId.create(),
            BookCopyId.create(),
            LocalDate.of(2026, 1, 1),
            LoanStatus.EXTENDED);

    assertThatThrownBy(closedLoan::extend).isInstanceOf(ExtensionNotAllowedException.class);
    assertThatThrownBy(extendedLoan::extend).isInstanceOf(ExtensionNotAllowedException.class);
  }
}
