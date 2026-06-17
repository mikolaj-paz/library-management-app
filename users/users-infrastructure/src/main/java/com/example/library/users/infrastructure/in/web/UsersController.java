package com.example.library.users.infrastructure.in.web;

import com.example.library.sharedkernel.identifier.ReaderAccountId;
import com.example.library.users.application.command.RegisterReaderAccount;
import com.example.library.users.application.command.UnblockReaderAccount;
import com.example.library.users.application.port.in.IRegisterReaderAccount;
import com.example.library.users.application.port.in.IUnblockReaderAccount;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UsersController {

  private final IRegisterReaderAccount registerReaderAccountService;
  private final IUnblockReaderAccount unblockReaderAccountService;

  public UsersController(
      IRegisterReaderAccount registerReaderAccountService,
      IUnblockReaderAccount unblockReaderAccountService) {
    this.registerReaderAccountService = registerReaderAccountService;
    this.unblockReaderAccountService = unblockReaderAccountService;
  }

  @PostMapping("/readers/register")
  public ResponseEntity<?> registerReaderAccount(@RequestBody RegisterReaderAccount request) {
    try {
      var results = registerReaderAccountService.registerReaderAccount(request);
      return ResponseEntity.ok(Map.of("readerAccountId", results.id().toString()));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  @PostMapping("/readers/unblock")
  public ResponseEntity<?> unblockReaderAccount(@RequestBody UnblockReaderAccountRequest request) {
    try {
      var command = new UnblockReaderAccount(ReaderAccountId.of(request.readerAccountId()));
      unblockReaderAccountService.unblockReaderAccount(command);
      return ResponseEntity.ok(Map.of("message", "Reader account unblocked successfully."));
    } catch (IllegalArgumentException e) {
      return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    } catch (Exception e) {
      return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
    }
  }

  record UnblockReaderAccountRequest(String readerAccountId) {}
}
