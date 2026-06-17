package com.example.library.users.application.port.in;

import com.example.library.users.application.command.UnblockReaderAccount;

public interface IUnblockReaderAccount {

  void unblockReaderAccount(UnblockReaderAccount command);
}
