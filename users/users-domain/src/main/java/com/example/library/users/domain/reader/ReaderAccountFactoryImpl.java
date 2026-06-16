package com.example.library.users.domain.reader;

import com.example.library.sharedkernel.identifier.ReaderAccountId;
import java.security.SecureRandom;
import java.util.stream.Collectors;

public class ReaderAccountFactoryImpl implements ReaderAccountFactory {

  private static final String PASSWORD_CHARACTERS =
      "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()_+-=[]{}|;:,.<>?";
  private static final SecureRandom RANDOM = new SecureRandom();

  private String generateRandomPassword() {
    return RANDOM
        .ints(12, 0, PASSWORD_CHARACTERS.length())
        .mapToObj(PASSWORD_CHARACTERS::charAt)
        .map(Object::toString)
        .collect(Collectors.joining());
  }

  @Override
  public ReaderAccount create(String name, String surname, String email, String telephone) {
    return ReaderAccount.of(
        ReaderAccountId.create(),
        email,
        name,
        surname,
        telephone,
        generateRandomPassword(),
        ReaderAccountStatus.ACTIVE);
  }

  @Override
  public ReaderAccount reconstitute(
      ReaderAccountId id,
      String name,
      String surname,
      String email,
      String telephone,
      String password,
      ReaderAccountStatus status) {
    return ReaderAccount.of(id, email, name, surname, telephone, password, status);
  }
}
