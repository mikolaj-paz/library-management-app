package com.example.library.users.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.event.ReaderAccountUnblocked;
import com.example.library.sharedkernel.identifier.ReaderAccountId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import com.example.library.users.application.command.UnblockReaderAccount;
import com.example.library.users.application.repository.ReaderAccountRepository;
import com.example.library.users.domain.reader.ReaderAccountFactoryImpl;
import com.example.library.users.domain.reader.ReaderAccountStatus;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UnblockingUserAccountTest {

  @Mock private ReaderAccountRepository readerAccountRepository;

  @Mock private DomainEventPublisher eventPublisher;

  @Test
  void should_unblock_reader_account_and_publish_event() {
    var readerAccountId = ReaderAccountId.create();
    var account =
        new ReaderAccountFactoryImpl()
            .reconstitute(
                readerAccountId,
                "Jane",
                "Doe",
                "jane.doe@example.com",
                "+48123456789",
                "initialPassword",
                ReaderAccountStatus.BLOCKED);
    account.pullDomainEvents();
    when(readerAccountRepository.find(readerAccountId)).thenReturn(Optional.of(account));
    var service = new UnblockingUserAccount(readerAccountRepository, eventPublisher);

    service.unblockReaderAccount(new UnblockReaderAccount(readerAccountId));

    assertThat(account.status()).isEqualTo(ReaderAccountStatus.ACTIVE);
    verify(readerAccountRepository).update(account);
    var eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue())
        .isInstanceOfSatisfying(
            ReaderAccountUnblocked.class,
            event -> assertThat(event.readerAccountId()).isEqualTo(readerAccountId));
  }

  @Test
  void should_throw_when_reader_account_does_not_exist() {
    var readerAccountId = ReaderAccountId.create();
    when(readerAccountRepository.find(readerAccountId)).thenReturn(Optional.empty());
    var service = new UnblockingUserAccount(readerAccountRepository, eventPublisher);

    assertThatThrownBy(() -> service.unblockReaderAccount(new UnblockReaderAccount(readerAccountId)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Reader account not found");

    verify(readerAccountRepository, never()).update(any());
    verify(eventPublisher, never()).publish(any());
  }
}
