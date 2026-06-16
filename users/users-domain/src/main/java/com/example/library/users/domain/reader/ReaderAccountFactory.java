package com.example.library.users.domain.reader;

import com.example.library.sharedkernel.identifier.ReaderAccountId;

public interface ReaderAccountFactory {

  ReaderAccount create(String name, String surname, String email, String telephone);

  ReaderAccount reconstitute(
      ReaderAccountId id,
      String name,
      String surname,
      String email,
      String telephone,
      String password,
      ReaderAccountStatus status);
}
