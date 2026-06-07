package com.example.library.lending.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.lending.application.command.LendBookCopy;
import com.example.library.lending.application.port.out.BookCopyRepository;
import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.application.port.out.ReaderRepository;
import com.example.library.lending.domain.copy.BookCopy;
import com.example.library.lending.domain.exception.BookCopyNotAvailableException;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.lending.domain.loan.Loan;
import com.example.library.lending.domain.reader.Reader;
import com.example.library.lending.domain.reader.ReaderStatus;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
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

  private LendBookCopyService service;
  private ReaderId readerId;
  private BookCopyId copyId;

  @BeforeEach
  void setUp() {
    service =
        new LendBookCopyService(
            loanRepository, bookCopyRepository, readerRepository, eventPublisher);
    readerId = ReaderId.create();
    copyId = BookCopyId.create();
  }

  @Test
  void should_create_loan_and_mark_copy_as_loaned_when_reader_can_lend_available_copy() {
    var copy = BookCopy.create(copyId, BookCopyStatus.AVAILABLE, null);
    givenActiveReader();
    givenActiveLoanCount(0);
    when(bookCopyRepository.find(copyId)).thenReturn(Optional.of(copy));

    var loanId = service.lend(new LendBookCopy(copyId, readerId));

    assertThat(loanId).isNotNull();
    var loanCaptor = ArgumentCaptor.forClass(Loan.class);
    verify(loanRepository).create(loanCaptor.capture());
    assertThat(loanCaptor.getValue().id()).isEqualTo(loanId);
    assertThat(loanCaptor.getValue().readerId()).isEqualTo(readerId);
    assertThat(loanCaptor.getValue().bookCopyId()).isEqualTo(copyId);
    verify(bookCopyRepository).update(copy);
    assertThat(copy.status()).isEqualTo(BookCopyStatus.LOANED);
    assertThat(copy.reservedBy()).isNull();
  }

  @Test
  void should_throw_when_reader_does_not_exist() {
    when(readerRepository.find(readerId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.lend(new LendBookCopy(copyId, readerId)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Reader not found");

    verify(loanRepository, never()).create(any());
  }

  @Test
  void should_throw_when_reader_is_blocked() {
    when(readerRepository.find(readerId))
        .thenReturn(Optional.of(Reader.create(readerId, ReaderStatus.BLOCKED)));

    assertThatThrownBy(() -> service.lend(new LendBookCopy(copyId, readerId)))
        .isInstanceOf(ReaderBlockedException.class);

    verify(loanRepository, never()).create(any());
  }

  @Test
  void should_throw_when_reader_reached_active_loan_limit() {
    givenActiveReader();
    givenActiveLoanCount(LoanLimitExceededException.MAX_ACTIVE_LOANS);

    assertThatThrownBy(() -> service.lend(new LendBookCopy(copyId, readerId)))
        .isInstanceOf(LoanLimitExceededException.class);

    verify(bookCopyRepository, never()).find(any());
    verify(loanRepository, never()).create(any());
  }

  @Test
  void should_throw_when_copy_does_not_exist() {
    givenActiveReader();
    givenActiveLoanCount(0);
    when(bookCopyRepository.find(copyId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> service.lend(new LendBookCopy(copyId, readerId)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Book copy not found");

    verify(loanRepository, never()).create(any());
  }

  @Test
  void should_throw_when_copy_is_not_available_for_reader() {
    var copy = BookCopy.create(copyId, BookCopyStatus.LOANED, null);
    givenActiveReader();
    givenActiveLoanCount(0);
    when(bookCopyRepository.find(copyId)).thenReturn(Optional.of(copy));

    assertThatThrownBy(() -> service.lend(new LendBookCopy(copyId, readerId)))
        .isInstanceOf(BookCopyNotAvailableException.class);

    verify(loanRepository, never()).create(any());
  }

  @Test
  void should_allow_loan_when_copy_is_reserved_for_same_reader() {
    var copy = BookCopy.create(copyId, BookCopyStatus.RESERVED, readerId);
    givenActiveReader();
    givenActiveLoanCount(0);
    when(bookCopyRepository.find(copyId)).thenReturn(Optional.of(copy));

    service.lend(new LendBookCopy(copyId, readerId));

    verify(loanRepository).create(any(Loan.class));
    verify(bookCopyRepository).update(copy);
    assertThat(copy.status()).isEqualTo(BookCopyStatus.LOANED);
  }

  private void givenActiveReader() {
    when(readerRepository.find(readerId))
        .thenReturn(Optional.of(Reader.create(readerId, ReaderStatus.ACTIVE)));
  }

  private void givenActiveLoanCount(int count) {
    when(loanRepository.countActiveLoansForReader(readerId)).thenReturn(count);
  }
}
