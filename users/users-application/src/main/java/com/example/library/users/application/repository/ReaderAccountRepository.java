package com.example.library.users.application.repository;

import com.example.library.sharedkernel.identifier.ReaderAccountId;
import com.example.library.users.application.port.out.ReaderAccountPersistancePort;
import com.example.library.users.domain.reader.ReaderAccount;
import java.util.Optional;

public class ReaderAccountRepository {

  private final ReaderAccountPersistancePort persistancePort;

  public ReaderAccountRepository(ReaderAccountPersistancePort persistancePort) {
    this.persistancePort = persistancePort;
  }

  public void create(ReaderAccount readerAccount) {
    persistancePort.create(readerAccount);
  }

  public Optional<ReaderAccount> findByEmail(String email) {
    return persistancePort.findByEmail(email);
  }

  public Optional<ReaderAccount> find(ReaderAccountId readerAccountId) {
    return persistancePort.find(readerAccountId);
  }

  public void update(ReaderAccount readerAccount) {
    persistancePort.update(readerAccount);
  }
}
