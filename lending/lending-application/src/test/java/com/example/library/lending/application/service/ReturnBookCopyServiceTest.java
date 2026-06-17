package com.example.library.lending.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.lending.application.command.ReturnBookCopy;
import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.lending.application.repository.LoanRepository;
import com.example.library.lending.domain.copy.BookCopyFactoryImpl;
import com.example.library.lending.domain.exception.LoanNotFoundException;
import com.example.library.lending.domain.loan.Loan;
import com.example.library.lending.domain.loan.LoanFactoryImpl;
import com.example.library.lending.domain.loan.LoanStatus;
import com.example.library.sharedkernel.event.BookCopyReturned;
import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ReturnBookCopyServiceTest {

  @Mock private LoanRepository loanRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private DomainEventPublisher eventPublisher;

  @Test
  void should_close_loan_mark_copy_available_and_publish_return_event() {
    var readerId = ReaderId.create();
    var bookCopyId = BookCopyId.create();
    var loan = activeLoan(readerId, bookCopyId, LocalDate.now().minusDays(1));
    var copy =
        new BookCopyFactoryImpl()
            .reconstitute(bookCopyId, BookCopyStatus.LOANED, null, BookId.create());
    when(loanRepository.findActiveLoan(bookCopyId)).thenReturn(Optional.of(loan));
    when(bookCopyRepository.find(bookCopyId)).thenReturn(Optional.of(copy));
    var service = new ReturnBookCopyService(loanRepository, bookCopyRepository, eventPublisher);

    service.returnCopy(new ReturnBookCopy(bookCopyId));

    assertThat(loan.status()).isEqualTo(LoanStatus.CLOSED);
    assertThat(copy.status()).isEqualTo(BookCopyStatus.AVAILABLE);
    verify(loanRepository).update(loan);
    verify(bookCopyRepository).update(copy);
    var eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue())
        .isInstanceOfSatisfying(
            BookCopyReturned.class,
            event -> {
              assertThat(event.readerId()).isEqualTo(readerId);
              assertThat(event.bookCopyId()).isEqualTo(bookCopyId);
              assertThat(event.isOverdue()).isTrue();
            });
  }

  @Test
  void should_throw_when_active_loan_does_not_exist() {
    var bookCopyId = BookCopyId.create();
    when(loanRepository.findActiveLoan(bookCopyId)).thenReturn(Optional.empty());
    var service = new ReturnBookCopyService(loanRepository, bookCopyRepository, eventPublisher);

    assertThatThrownBy(() -> service.returnCopy(new ReturnBookCopy(bookCopyId)))
        .isInstanceOf(LoanNotFoundException.class);

    verify(bookCopyRepository, never()).update(any());
    verify(eventPublisher, never()).publish(any());
  }

  private Loan activeLoan(ReaderId readerId, BookCopyId bookCopyId, LocalDate dueDate) {
    return new LoanFactoryImpl()
        .reconstitute(LoanId.create(), readerId, bookCopyId, dueDate, LoanStatus.ACTIVE);
  }
}
