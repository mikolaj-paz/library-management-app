package com.example.library.catalog.application.port.in;

import com.example.library.catalog.application.command.RemoveBookCopy;

public interface IRemoveBookCopy {

  void removeBookCopy(RemoveBookCopy command);
}
