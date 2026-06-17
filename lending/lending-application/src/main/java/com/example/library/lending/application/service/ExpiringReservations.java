package com.example.library.lending.application.service;

import com.example.library.lending.application.port.in.IExpireReservations;
import com.example.library.lending.application.repository.ReservationRepository;
import com.example.library.lending.domain.reservation.Reservation;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import java.util.List;

public class ExpiringReservations implements IExpireReservations {

  private final ReservationRepository reservationRepository;
  private final DomainEventPublisher eventPublisher;

  public ExpiringReservations(
      ReservationRepository reservationRepository, DomainEventPublisher eventPublisher) {
    this.reservationRepository = reservationRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public void expireReservations() {

    List<Reservation> expiredReservations = reservationRepository.findExpiredReservations();

    for (Reservation reservation : expiredReservations) {
      reservation.expire();
      reservation.pullDomainEvents().forEach(eventPublisher::publish);
    }
  }
}
