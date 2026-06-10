package com.example.library.lending.application.port.in;

import com.example.library.lending.application.command.ReserveBook;
import com.example.library.sharedkernel.identifier.ReservationId;

public interface IReserveBook {

  ReservationId reserve(ReserveBook command);
}
