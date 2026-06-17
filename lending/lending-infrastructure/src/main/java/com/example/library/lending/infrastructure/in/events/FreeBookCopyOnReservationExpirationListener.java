package com.example.library.lending.infrastructure.in.events;

import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.sharedkernel.event.ReservationExpired;
import org.springframework.context.event.EventListener;

public class FreeBookCopyOnReservationExpirationListener {

  private final BookCopyRepository bookCopyRepository;

  public FreeBookCopyOnReservationExpirationListener(BookCopyRepository bookCopyRepository) {
    this.bookCopyRepository = bookCopyRepository;
  }

  @EventListener
  public void on(ReservationExpired event) {
    var bookCopy =
        bookCopyRepository
            .find(event.bookCopyId())
            .orElseThrow(
                () -> new IllegalStateException("Book copy not found: " + event.bookCopyId()));

    bookCopy.expireReservation();
    bookCopyRepository.update(bookCopy);
  }
}
