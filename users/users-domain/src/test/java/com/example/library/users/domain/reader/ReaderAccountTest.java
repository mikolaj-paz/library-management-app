package com.example.library.users.domain.reader;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.sharedkernel.event.ReaderAccountRegistered;
import com.example.library.sharedkernel.identifier.ReaderAccountId;
import org.junit.jupiter.api.Test;

class ReaderAccountTest {

  @Test
  void should_create_active_reader_account_with_generated_password() {
    var account =
        new ReaderAccountFactoryImpl()
            .create("Jane", "Doe", "jane.doe@example.com", "+48123456789");

    assertThat(account.id()).isNotNull();
    assertThat(account.name()).isEqualTo("Jane");
    assertThat(account.surname()).isEqualTo("Doe");
    assertThat(account.email()).isEqualTo("jane.doe@example.com");
    assertThat(account.telephone()).isEqualTo("+48123456789");
    assertThat(account.password()).hasSize(12);
    assertThat(account.status()).isEqualTo(ReaderAccountStatus.ACTIVE);
  }

  @Test
  void should_register_reader_account_registered_event() {
    var account =
        new ReaderAccountFactoryImpl()
            .create("Jane", "Doe", "jane.doe@example.com", "+48123456789");

    assertThat(account.pullDomainEvents())
        .singleElement()
        .isInstanceOfSatisfying(
            ReaderAccountRegistered.class,
            event -> {
              assertThat(event.getReaderAccountId()).isEqualTo(account.id());
              assertThat(event.getEmail()).isEqualTo("jane.doe@example.com");
              assertThat(event.getName()).isEqualTo("Jane");
              assertThat(event.getSurname()).isEqualTo("Doe");
              assertThat(event.getTelephone()).isEqualTo("+48123456789");
            });
  }

  @Test
  void should_reconstitute_reader_account() {
    var id = ReaderAccountId.create();

    var account =
        new ReaderAccountFactoryImpl()
            .reconstitute(
                id,
                "John",
                "Smith",
                "john.smith@example.com",
                "+48987654321",
                "initialPassword",
                ReaderAccountStatus.BLOCKED);

    assertThat(account.id()).isEqualTo(id);
    assertThat(account.password()).isEqualTo("initialPassword");
    assertThat(account.status()).isEqualTo(ReaderAccountStatus.BLOCKED);
  }
}
