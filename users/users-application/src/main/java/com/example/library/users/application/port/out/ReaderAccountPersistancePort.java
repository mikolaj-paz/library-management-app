package com.example.library.users.application.port.out;

import com.example.library.users.domain.reader.ReaderAccount;
import java.util.Optional;

public interface ReaderAccountPersistancePort {

  void create(ReaderAccount readerAccount);

  Optional<ReaderAccount> findByEmail(String email);
}
