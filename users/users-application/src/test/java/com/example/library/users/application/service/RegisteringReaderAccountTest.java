package com.example.library.users.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.event.ReaderAccountRegistered;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import com.example.library.users.application.command.RegisterReaderAccount;
import com.example.library.users.application.repository.ReaderAccountRepository;
import com.example.library.users.domain.reader.ReaderAccount;
import com.example.library.users.domain.reader.ReaderAccountFactoryImpl;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegisteringReaderAccountTest {

  @Mock private ReaderAccountRepository readerAccountRepository;

  @Mock private DomainEventPublisher eventPublisher;

  @Test
  void should_create_reader_account_when_email_is_unique() {
    var command = new RegisterReaderAccount("Jane", "Doe", "jane.doe@example.com", "+48123456789");
    when(readerAccountRepository.findByEmail(command.email())).thenReturn(Optional.empty());
    var service =
        new RegisteringReaderAccount(
            readerAccountRepository, new ReaderAccountFactoryImpl(), eventPublisher);

    var account = service.registerReaderAccount(command);

    assertThat(account.id()).isNotNull();
    assertThat(account.email()).isEqualTo(command.email());
    var accountCaptor = ArgumentCaptor.forClass(ReaderAccount.class);
    verify(readerAccountRepository).create(accountCaptor.capture());
    assertThat(accountCaptor.getValue()).isEqualTo(account);
    var eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue())
        .isInstanceOfSatisfying(
            ReaderAccountRegistered.class,
            event -> assertThat(event.getEmail()).isEqualTo(command.email()));
  }

  @Test
  void should_reject_duplicate_email() {
    var existingAccount =
        new ReaderAccountFactoryImpl()
            .create("Jane", "Doe", "jane.doe@example.com", "+48123456789");
    var command = new RegisterReaderAccount("Jane", "Doe", "jane.doe@example.com", "+48123456789");
    when(readerAccountRepository.findByEmail(command.email()))
        .thenReturn(Optional.of(existingAccount));
    var service =
        new RegisteringReaderAccount(
            readerAccountRepository, new ReaderAccountFactoryImpl(), eventPublisher);

    assertThatThrownBy(() -> service.registerReaderAccount(command))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("already exists");

    verify(readerAccountRepository, never()).create(any());
    verify(eventPublisher, never()).publish(any());
  }
}
