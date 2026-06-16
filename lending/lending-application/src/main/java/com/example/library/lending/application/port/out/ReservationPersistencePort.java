package com.example.library.lending.application.port.out;

import com.example.library.lending.domain.reservation.Reservation;

public interface ReservationPersistencePort {

  void create(Reservation reservation);
}
