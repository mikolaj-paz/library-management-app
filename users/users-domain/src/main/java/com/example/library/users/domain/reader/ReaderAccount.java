package com.example.library.users.domain.reader;

import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.event.ReaderAccountRegisteredEvent;
import com.example.library.sharedkernel.identifier.ReaderAccountId;

public class ReaderAccount extends AggregateRoot<ReaderAccountId> {

  private final String email;
  private final String name;
  private final String surname;
  private final String telephone;
  private final String password;
  private final ReaderAccountStatus status;

  private ReaderAccount(
      ReaderAccountId id,
      String email,
      String name,
      String surname,
      String telephone,
      String password,
      ReaderAccountStatus status) {
    super(id);
    this.email = email;
    this.name = name;
    this.surname = surname;
    this.telephone = telephone;
    this.password = password;
    this.status = status;
    registerEvent(new ReaderAccountRegisteredEvent(id, email, name, surname, telephone));
  }

  static ReaderAccount of(
      ReaderAccountId id,
      String email,
      String name,
      String surname,
      String telephone,
      String password,
      ReaderAccountStatus status) {
    return new ReaderAccount(id, email, name, surname, telephone, password, status);
  }

  public String email() {
    return email;
  }

  public String name() {
    return name;
  }

  public String surname() {
    return surname;
  }

  public String telephone() {
    return telephone;
  }

  public String password() {
    return password;
  }

  public ReaderAccountStatus status() {
    return status;
  }
}
