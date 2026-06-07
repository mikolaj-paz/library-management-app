package com.example.library.lending.application.port.out;

import com.example.library.lending.domain.reservation.Reservation;

public interface ReservationRepository {
  void create(Reservation reservation);
}
