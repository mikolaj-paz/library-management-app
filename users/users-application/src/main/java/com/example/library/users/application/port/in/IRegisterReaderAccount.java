package com.example.library.users.application.port.in;

import com.example.library.users.application.command.RegisterReaderAccount;
import com.example.library.users.domain.reader.ReaderAccount;

public interface IRegisterReaderAccount {

  ReaderAccount registerReaderAccount(RegisterReaderAccount command);
}
