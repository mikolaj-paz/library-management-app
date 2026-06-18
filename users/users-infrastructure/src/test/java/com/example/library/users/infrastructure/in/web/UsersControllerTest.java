package com.example.library.users.infrastructure.in.web;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.sharedkernel.identifier.ReaderAccountId;
import com.example.library.users.application.command.RegisterReaderAccount;
import com.example.library.users.application.command.UnblockReaderAccount;
import com.example.library.users.application.port.in.IRegisterReaderAccount;
import com.example.library.users.application.port.in.IUnblockReaderAccount;
import com.example.library.users.domain.reader.ReaderAccountFactoryImpl;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UsersControllerTest {

  @Mock private IRegisterReaderAccount registerReaderAccountService;

  @Mock private IUnblockReaderAccount unblockReaderAccountService;

  @Test
  void should_return_reader_account_id_when_registration_succeeds() {
    var account =
        new ReaderAccountFactoryImpl()
            .create("Jane", "Doe", "jane.doe@example.com", "+48123456789");
    when(registerReaderAccountService.registerReaderAccount(any())).thenReturn(account);
    var controller = new UsersController(registerReaderAccountService, unblockReaderAccountService);

    var response = controller.registerReaderAccount(request());

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody())
        .isEqualTo(Map.of("readerAccountId", account.id().value().toString()));
  }

  @Test
  void should_return_bad_request_when_email_already_exists() {
    when(registerReaderAccountService.registerReaderAccount(any()))
        .thenThrow(new IllegalArgumentException("Reader account already exists"));
    var controller = new UsersController(registerReaderAccountService, unblockReaderAccountService);

    var response = controller.registerReaderAccount(request());

    assertThat(response.getStatusCode().value()).isEqualTo(400);
    assertThat(response.getBody().toString()).contains("error");
  }

  @Test
  void should_return_success_when_reader_account_is_unblocked() {
    var readerAccountId = ReaderAccountId.create();
    var controller = new UsersController(registerReaderAccountService, unblockReaderAccountService);

    var response =
        controller.unblockReaderAccount(
            new UsersController.UnblockReaderAccountRequest(readerAccountId.value().toString()));

    assertThat(response.getStatusCode().value()).isEqualTo(200);
    assertThat(response.getBody())
        .isEqualTo(Map.of("message", "Reader account unblocked successfully."));
    var commandCaptor = ArgumentCaptor.forClass(UnblockReaderAccount.class);
    verify(unblockReaderAccountService).unblockReaderAccount(commandCaptor.capture());
    assertThat(commandCaptor.getValue().readerAccountId()).isEqualTo(readerAccountId);
  }

  @Test
  void should_return_bad_request_when_reader_account_to_unblock_does_not_exist() {
    var readerAccountId = ReaderAccountId.create();
    doThrow(new IllegalArgumentException("Reader account not found."))
        .when(unblockReaderAccountService)
        .unblockReaderAccount(any());
    var controller = new UsersController(registerReaderAccountService, unblockReaderAccountService);

    var response =
        controller.unblockReaderAccount(
            new UsersController.UnblockReaderAccountRequest(readerAccountId.value().toString()));

    assertThat(response.getStatusCode().value()).isEqualTo(400);
    assertThat(response.getBody().toString()).contains("error");
  }

  private RegisterReaderAccount request() {
    return new RegisterReaderAccount("Jane", "Doe", "jane.doe@example.com", "+48123456789");
  }
}
