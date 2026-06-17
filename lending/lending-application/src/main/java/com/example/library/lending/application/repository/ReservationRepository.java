package com.example.library.lending.application.repository;

import com.example.library.lending.application.port.out.ReservationPersistencePort;
import com.example.library.lending.domain.reservation.Reservation;
import java.util.List;

public class ReservationRepository {

  private final ReservationPersistencePort persistencePort;

  public ReservationRepository(ReservationPersistencePort persistencePort) {
    this.persistencePort = persistencePort;
  }

  public void create(Reservation reservation) {
    persistencePort.create(reservation);
  }

  public List<Reservation> findExpiredReservations() {
    return persistencePort.findExpiredReservations();
  }
}
