package com.example.library.users.application.repository;

import com.example.library.users.application.port.out.ReaderAccountPersistancePort;
import com.example.library.users.domain.reader.ReaderAccount;
import java.util.Optional;

public class ReaderAcountRepository {

  private final ReaderAccountPersistancePort persistancePort;

  public ReaderAcountRepository(ReaderAccountPersistancePort persistancePort) {
    this.persistancePort = persistancePort;
  }

  public void create(ReaderAccount readerAccount) {
    persistancePort.create(readerAccount);
  }

  public Optional<ReaderAccount> findByEmail(String email) {
    return persistancePort.findByEmail(email);
  }
}
