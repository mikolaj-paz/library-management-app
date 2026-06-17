package com.example.library.lending.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.lending.application.command.ExtendLoanCommand;
import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.lending.application.repository.BookRepository;
import com.example.library.lending.application.repository.LoanRepository;
import com.example.library.lending.domain.book.BookFactoryImpl;
import com.example.library.lending.domain.copy.BookCopyFactoryImpl;
import com.example.library.lending.domain.exception.ExtensionNotAllowedException;
import com.example.library.lending.domain.loan.LoanFactoryImpl;
import com.example.library.lending.domain.loan.LoanStatus;
import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.event.LoanExtended;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ExtendLoanServiceTest {

  @Mock private LoanRepository loanRepository;

  @Mock private BookRepository bookRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private DomainEventPublisher eventPublisher;

  @Test
  void should_extend_loan_when_book_has_no_queued_reader() {
    var readerId = ReaderId.create();
    var bookId = BookId.create();
    var bookCopyId = BookCopyId.create();
    var loan =
        new LoanFactoryImpl()
            .reconstitute(
                LoanId.create(), readerId, bookCopyId, LocalDate.of(2026, 1, 1), LoanStatus.ACTIVE);
    var bookCopy =
        new BookCopyFactoryImpl().reconstitute(bookCopyId, BookCopyStatus.LOANED, null, bookId);
    var book = new BookFactoryImpl().reconstitute(bookId, List.of());
    when(loanRepository.find(loan.id())).thenReturn(Optional.of(loan));
    when(bookCopyRepository.find(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(bookRepository.find(bookId)).thenReturn(Optional.of(book));
    var service =
        new ExtendingLoan(loanRepository, bookRepository, bookCopyRepository, eventPublisher);

    service.extendLoan(new ExtendLoanCommand(loan.id(), readerId));

    assertThat(loan.status()).isEqualTo(LoanStatus.EXTENDED);
    assertThat(loan.dueDate()).isEqualTo(LocalDate.of(2026, 1, 15));
    verify(loanRepository).update(loan);
    var eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue())
        .isInstanceOfSatisfying(
            LoanExtended.class,
            event -> {
              assertThat(event.loanId()).isEqualTo(loan.id());
              assertThat(event.readerId()).isEqualTo(readerId);
              assertThat(event.bookCopyId()).isEqualTo(bookCopyId);
              assertThat(event.newDueDate()).isEqualTo(loan.dueDate());
            });
  }

  @Test
  void should_reject_extension_when_book_has_queued_reader() {
    var readerId = ReaderId.create();
    var queuedReaderId = ReaderId.create();
    var bookId = BookId.create();
    var bookCopyId = BookCopyId.create();
    var loan =
        new LoanFactoryImpl()
            .reconstitute(
                LoanId.create(), readerId, bookCopyId, LocalDate.of(2026, 1, 1), LoanStatus.ACTIVE);
    var bookCopy =
        new BookCopyFactoryImpl().reconstitute(bookCopyId, BookCopyStatus.LOANED, null, bookId);
    var book = new BookFactoryImpl().reconstitute(bookId, List.of(queuedReaderId));
    when(loanRepository.find(loan.id())).thenReturn(Optional.of(loan));
    when(bookCopyRepository.find(bookCopyId)).thenReturn(Optional.of(bookCopy));
    when(bookRepository.find(bookId)).thenReturn(Optional.of(book));
    var service =
        new ExtendingLoan(loanRepository, bookRepository, bookCopyRepository, eventPublisher);

    assertThatThrownBy(() -> service.extendLoan(new ExtendLoanCommand(loan.id(), readerId)))
        .isInstanceOf(ExtensionNotAllowedException.class);

    verify(loanRepository, never()).update(any());
    verify(eventPublisher, never()).publish(any());
  }
}
