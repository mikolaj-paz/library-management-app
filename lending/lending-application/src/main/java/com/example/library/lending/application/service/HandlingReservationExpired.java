package com.example.library.lending.application.service;

import com.example.library.lending.application.port.in.IHandleReservationExpired;
import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.sharedkernel.identifier.BookCopyId;

public class HandlingReservationExpired implements IHandleReservationExpired {

  private final BookCopyRepository bookCopyRepository;

  public HandlingReservationExpired(BookCopyRepository bookCopyRepository) {
    this.bookCopyRepository = bookCopyRepository;
  }

  @Override
  public void handleReservationExpired(BookCopyId bookCopyId) {
    var bookCopy =
        bookCopyRepository
            .find(bookCopyId)
            .orElseThrow(() -> new IllegalStateException("Book copy not found: " + bookCopyId));

    bookCopy.expireReservation();
    bookCopyRepository.update(bookCopy);
  }
}
