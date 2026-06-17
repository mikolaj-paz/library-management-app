package com.example.library.lending.application.port.in;

import com.example.library.sharedkernel.identifier.BookCopyId;

public interface IHandleReservationExpired {

  void handleReservationExpired(BookCopyId bookCopyId);
}
