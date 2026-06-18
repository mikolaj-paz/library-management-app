package com.example.library.sharedkernel.event;

import com.example.library.sharedkernel.identifier.ReaderAccountId;

public class ReaderAccountRegistered extends DomainEvent {

  private final ReaderAccountId readerAccountId;
  private final String email;
  private final String name;
  private final String surname;
  private final String telephone;

  public ReaderAccountRegistered(
      ReaderAccountId id, String email, String name, String surname, String telephone) {
    super();
    this.readerAccountId = id;
    this.email = email;
    this.name = name;
    this.surname = surname;
    this.telephone = telephone;
  }

  public ReaderAccountId getReaderAccountId() {
    return readerAccountId;
  }

  public String getEmail() {
    return email;
  }

  public String getName() {
    return name;
  }

  public String getSurname() {
    return surname;
  }

  public String getTelephone() {
    return telephone;
  }
}
