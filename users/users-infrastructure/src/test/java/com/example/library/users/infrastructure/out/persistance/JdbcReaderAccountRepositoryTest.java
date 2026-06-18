package com.example.library.users.infrastructure.out.persistance;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.users.domain.reader.ReaderAccountFactoryImpl;
import com.example.library.users.domain.reader.ReaderAccountStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

class JdbcReaderAccountRepositoryTest {

  private JdbcTemplate jdbc;
  private JdbcReaderAccountRepository repository;

  @BeforeEach
  void setUp() {
    jdbc = createJdbcTemplate();
    repository = new JdbcReaderAccountRepository(jdbc, new ReaderAccountFactoryImpl());
  }

  @Test
  void should_insert_reader_account() {
    var account =
        new ReaderAccountFactoryImpl()
            .create("Jane", "Doe", "jane.doe@example.com", "+48123456789");

    repository.create(account);

    var row =
        jdbc.queryForMap("SELECT * FROM readers WHERE id = ?", account.id().value().toString());
    assertThat(row.get("name")).isEqualTo("Jane");
    assertThat(row.get("surname")).isEqualTo("Doe");
    assertThat(row.get("email")).isEqualTo("jane.doe@example.com");
    assertThat(row.get("telephone")).isEqualTo("+48123456789");
    assertThat(row.get("password")).isEqualTo(account.password());
    assertThat(row.get("status")).isEqualTo("ACTIVE");
  }

  @Test
  void should_find_reader_account_by_email() {
    insertReader("John", "Smith", "john.smith@example.com", "+48987654321", "secret");

    var account = repository.findByEmail("john.smith@example.com");

    assertThat(account).isPresent();
    assertThat(account.get().name()).isEqualTo("John");
    assertThat(account.get().surname()).isEqualTo("Smith");
    assertThat(account.get().email()).isEqualTo("john.smith@example.com");
    assertThat(account.get().password()).isEqualTo("secret");
    assertThat(account.get().status()).isEqualTo(ReaderAccountStatus.ACTIVE);
  }

  @Test
  void should_return_empty_when_email_does_not_exist() {
    assertThat(repository.findByEmail("missing@example.com")).isEmpty();
  }

  private JdbcTemplate createJdbcTemplate() {
    var dataSource = new SingleConnectionDataSource("jdbc:sqlite::memory:", true);
    var template = new JdbcTemplate(dataSource);
    template.execute(
        """
            CREATE TABLE readers (
                id TEXT PRIMARY KEY,
                name TEXT NOT NULL,
                surname TEXT NOT NULL,
                email TEXT NOT NULL UNIQUE,
                telephone TEXT NOT NULL,
                password TEXT NOT NULL,
                status TEXT NOT NULL
            )
            """);
    return template;
  }

  private void insertReader(
      String name, String surname, String email, String telephone, String password) {
    var account =
        new ReaderAccountFactoryImpl()
            .reconstitute(
                com.example.library.sharedkernel.identifier.ReaderAccountId.create(),
                name,
                surname,
                email,
                telephone,
                password,
                ReaderAccountStatus.ACTIVE);
    repository.create(account);
  }
}
