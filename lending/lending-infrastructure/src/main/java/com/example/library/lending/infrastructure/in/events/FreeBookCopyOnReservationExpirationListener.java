package com.example.library.lending.infrastructure.in.events;

import com.example.library.lending.application.port.in.IHandleReservationExpired;
import com.example.library.sharedkernel.event.ReservationExpired;
import org.springframework.context.event.EventListener;

public class FreeBookCopyOnReservationExpirationListener {

  private final IHandleReservationExpired handleReservationExpired;

  public FreeBookCopyOnReservationExpirationListener(
      IHandleReservationExpired handleReservationExpired) {
    this.handleReservationExpired = handleReservationExpired;
  }

  @EventListener
  public void on(ReservationExpired event) {
    handleReservationExpired.handleReservationExpired(event.bookCopyId());
  }
}
