package com.example.library.users.application.port.out;

import com.example.library.sharedkernel.identifier.ReaderAccountId;
import com.example.library.users.domain.reader.ReaderAccount;
import java.util.Optional;

public interface ReaderAccountPersistancePort {

  void create(ReaderAccount readerAccount);

  Optional<ReaderAccount> findByEmail(String email);

  Optional<ReaderAccount> find(ReaderAccountId readerAccountId);

  void update(ReaderAccount readerAccount);
}
