package com.example.library.lending.infrastructure.in.scheduler;

import com.example.library.lending.application.port.in.IExpireReservations;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ExpiringReservationsScheduler {

  private final IExpireReservations expireReservationsService;

  public ExpiringReservationsScheduler(IExpireReservations expireReservationsService) {
    this.expireReservationsService = expireReservationsService;
  }

  @Scheduled(cron = "0 0/15 * * * ?")
  public void expireReservations() {
    expireReservationsService.expireReservations();
  }
}
