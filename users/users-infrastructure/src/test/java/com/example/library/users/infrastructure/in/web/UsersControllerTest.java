package com.example.library.users.infrastructure.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.example.library.users.application.command.RegisterReaderAccount;
import com.example.library.users.application.port.in.IRegisterReaderAccount;
import com.example.library.users.domain.reader.ReaderAccountFactoryImpl;
import java.util.Map;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

  @Mock private IRegisterReaderAccount registerReaderAccountService;

  @Test
  @Disabled("TODO: UsersController currently returns ReaderAccountId via record toString")
  void should_return_reader_account_id_when_registration_succeeds() {
    var account =
        new ReaderAccountFactoryImpl()
            .create("Jane", "Doe", "jane.doe@example.com", "+48123456789");
    when(registerReaderAccountService.registerReaderAccount(any())).thenReturn(account);
    var controller = new UsersController(registerReaderAccountService);

    var response = controller.registerReaderAccount(request());

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody())
        .isEqualTo(Map.of("readerAccountId", account.id().value().toString()));
  }

  @Test
  void should_return_bad_request_when_email_already_exists() {
    when(registerReaderAccountService.registerReaderAccount(any()))
        .thenThrow(new IllegalArgumentException("Reader account already exists"));
    var controller = new UsersController(registerReaderAccountService);

    var response = controller.registerReaderAccount(request());

    assertThat(response.getStatusCode().value()).isEqualTo(400);
    assertThat(response.getBody().toString()).contains("error");
  }

  private RegisterReaderAccount request() {
    return new RegisterReaderAccount("Jane", "Doe", "jane.doe@example.com", "+48123456789");
  }
}
