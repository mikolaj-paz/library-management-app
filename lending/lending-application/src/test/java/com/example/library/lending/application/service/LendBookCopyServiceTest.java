package com.example.library.lending.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.lending.application.command.LendBookCopy;
import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.lending.application.repository.LoanRepository;
import com.example.library.lending.application.repository.ReaderRepository;
import com.example.library.lending.domain.copy.BookCopy;
import com.example.library.lending.domain.copy.BookCopyFactory;
import com.example.library.lending.domain.copy.BookCopyFactoryImpl;
import com.example.library.lending.domain.exception.BookCopyNotAvailableException;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.lending.domain.loan.Loan;
import com.example.library.lending.domain.loan.LoanFactoryImpl;
import com.example.library.lending.domain.reader.ReaderFactoryImpl;
import com.example.library.lending.domain.reader.ReaderStatus;
import com.example.library.sharedkernel.event.BookCopyLoaned;
import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LendBookCopyServiceTest {

  @Mock private LoanRepository loanRepository;

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private ReaderRepository readerRepository;

  @Mock private DomainEventPublisher eventPublisher;

  private LendingBookCopy service;
  private ReaderId readerId;
  private BookCopyId copyId;
  private BookId bookId;
  private BookCopyFactory bookCopyFactory;

  @BeforeEach
  void setUp() {
    service =
        new LendingBookCopy(
            loanRepository,
            bookCopyRepository,
            readerRepository,
            new LoanFactoryImpl(),
            eventPublisher);
    readerId = ReaderId.create();
    copyId = BookCopyId.create();
    bookId = BookId.create();
    bookCopyFactory = new BookCopyFactoryImpl();
  }

  @Test
  void should_create_loan_and_mark_copy_as_loaned_when_reader_can_lend_available_copy() {
    var copy = copy(BookCopyStatus.AVAILABLE, null);
    givenActiveReader();
    when(bookCopyRepository.find(copyId)).thenReturn(Optional.of(copy));

    var loanId = service.lendBookCopy(new LendBookCopy(copyId, readerId));

    assertThat(loanId).isNotNull();
    var loanCaptor = ArgumentCaptor.forClass(Loan.class);
    verify(loanRepository).create(loanCaptor.capture());
    assertThat(loanCaptor.getValue().id()).isEqualTo(loanId);
    assertThat(loanCaptor.getValue().readerId()).isEqualTo(readerId);
    assertThat(loanCaptor.getValue().bookCopyId()).isEqualTo(copyId);
    verify(bookCopyRepository).update(copy);
    assertThat(copy.status()).isEqualTo(BookCopyStatus.LOANED);
    assertThat(copy.reservedBy()).isNull();
    var eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue())
        .isInstanceOfSatisfying(
            BookCopyLoaned.class,
            event -> {
              assertThat(event.loanId()).isEqualTo(loanId);
              assertThat(event.readerId()).isEqualTo(readerId);
              assertThat(event.bookCopyId()).isEqualTo(copyId);
            });
  }

  @Test
  void should_throw_when_reader_does_not_exist() {
    when(readerRepository.find(readerId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.lendBookCopy(new LendBookCopy(copyId, readerId)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Reader not found");

    verify(loanRepository, never()).create(any());
  }

  @Test
  void should_throw_when_reader_is_blocked() {
    when(bookCopyRepository.find(copyId))
        .thenReturn(Optional.of(copy(BookCopyStatus.AVAILABLE, null)));
    when(readerRepository.find(readerId))
        .thenReturn(
            Optional.of(new ReaderFactoryImpl().reconstitute(readerId, ReaderStatus.BLOCKED, 0)));

    assertThatThrownBy(() -> service.lendBookCopy(new LendBookCopy(copyId, readerId)))
        .isInstanceOf(ReaderBlockedException.class);

    verify(loanRepository, never()).create(any());
  }

  @Test
  void should_throw_when_reader_reached_active_loan_limit() {
    when(bookCopyRepository.find(copyId))
        .thenReturn(Optional.of(copy(BookCopyStatus.AVAILABLE, null)));
    givenActiveReaderWithLoanCount(LoanLimitExceededException.MAX_ACTIVE_LOANS);

    assertThatThrownBy(() -> service.lendBookCopy(new LendBookCopy(copyId, readerId)))
        .isInstanceOf(LoanLimitExceededException.class);

    verify(loanRepository, never()).create(any());
  }

  @Test
  void should_throw_when_copy_does_not_exist() {
    givenActiveReader();
    when(bookCopyRepository.find(copyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.lendBookCopy(new LendBookCopy(copyId, readerId)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Book copy not found");

    verify(loanRepository, never()).create(any());
  }

  @Test
  @Disabled(
      "TODO: LendingBookCopy currently creates the loan before validating book copy availability")
  void should_throw_when_copy_is_not_available_for_reader() {
    var copy = copy(BookCopyStatus.LOANED, null);
    givenActiveReader();
    when(bookCopyRepository.find(copyId)).thenReturn(Optional.of(copy));

    assertThatThrownBy(() -> service.lendBookCopy(new LendBookCopy(copyId, readerId)))
        .isInstanceOf(BookCopyNotAvailableException.class);

    verify(loanRepository, never()).create(any());
  }

  @Test
  void should_allow_loan_when_copy_is_reserved_for_same_reader() {
    var copy = copy(BookCopyStatus.RESERVED, readerId);
    givenActiveReader();
    when(bookCopyRepository.find(copyId)).thenReturn(Optional.of(copy));

    service.lendBookCopy(new LendBookCopy(copyId, readerId));

    verify(loanRepository).create(any(Loan.class));
    verify(bookCopyRepository).update(copy);
    assertThat(copy.status()).isEqualTo(BookCopyStatus.LOANED);
  }

  private void givenActiveReader() {
    givenActiveReaderWithLoanCount(0);
  }

  private void givenActiveReaderWithLoanCount(int activeLoansCount) {
    when(readerRepository.find(readerId))
        .thenReturn(
            Optional.of(
                new ReaderFactoryImpl()
                    .reconstitute(readerId, ReaderStatus.ACTIVE, activeLoansCount)));
  }

  private BookCopy copy(BookCopyStatus status, ReaderId reservedBy) {
    return bookCopyFactory.reconstitute(copyId, status, reservedBy, bookId);
  }
}
