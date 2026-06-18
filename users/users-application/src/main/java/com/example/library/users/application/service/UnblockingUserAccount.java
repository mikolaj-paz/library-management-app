package com.example.library.users.application.service;

import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import com.example.library.users.application.command.UnblockReaderAccount;
import com.example.library.users.application.port.in.IUnblockReaderAccount;
import com.example.library.users.application.repository.ReaderAccountRepository;

public class UnblockingUserAccount implements IUnblockReaderAccount {

  private final ReaderAccountRepository readerAccountRepository;
  private final DomainEventPublisher eventPublisher;

  public UnblockingUserAccount(
      ReaderAccountRepository readerAccountRepository, DomainEventPublisher eventPublisher) {
    this.readerAccountRepository = readerAccountRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void unblockReaderAccount(UnblockReaderAccount command) {

    // 2. System pobiera dane konta oraz historię blokad z bazy danych.
    var readerAccount =
        readerAccountRepository
            .find(command.readerAccountId())
            .orElseThrow(() -> new IllegalArgumentException("Reader account not found."));

    // 3. Status konta czytelnika zostaje ustawiony na „Aktywne”.
    readerAccount.changeStatusToActive();

    // 4. Aktualizacja statusu konta i zapisanie zmian w bazie danych użytkowników.
    readerAccountRepository.update(readerAccount);

    readerAccount.pullDomainEvents().forEach(eventPublisher::publish);
  }
}
