package com.example.library.lending.application.port.in;

import com.example.library.lending.application.command.ReserveBookCopy;
import com.example.library.sharedkernel.identifier.ReservationId;

public interface IReserveBookCopy {

  ReservationId reserveBookCopy(ReserveBookCopy command);
}
