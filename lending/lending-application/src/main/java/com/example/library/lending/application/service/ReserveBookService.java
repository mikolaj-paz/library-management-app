package com.example.library.lending.application.service;

import com.example.library.lending.application.command.ReserveBook;
import com.example.library.lending.application.port.in.IReserveBook;
import com.example.library.lending.application.port.out.BookCopyRepository;
import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.application.port.out.ReaderRepository;
import com.example.library.lending.application.port.out.ReservationRepository;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.NoAvailableBookCopyException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.lending.domain.reservation.Reservation;
import com.example.library.lending.domain.reservation.ReservationId;
import com.example.library.sharedkernel.identifier.ReaderId;

public class ReserveBookService implements IReserveBook {

  private final ReaderRepository readerRepository;
  private final LoanRepository loanRepository;
  private final BookCopyRepository bookCopyRepository;
  private final ReservationRepository reservationRepository;

  private void verifyReaderEligibility(ReaderId readerId) {
    var reader =
        readerRepository
            .find(readerId)
            .orElseThrow(() -> new IllegalArgumentException("Reader not found: " + readerId));

    if (reader.isBlocked()) throw new ReaderBlockedException(readerId);

    int activeLoans = loanRepository.countActiveLoansForReader(readerId);
    if (activeLoans >= LoanLimitExceededException.MAX_ACTIVE_LOANS)
      throw new LoanLimitExceededException(readerId);
  }

  public ReserveBookService(
      ReaderRepository readerRepository,
      LoanRepository loanRepository,
      BookCopyRepository bookCopyRepository,
      ReservationRepository reservationRepository) {
    this.readerRepository = readerRepository;
    this.loanRepository = loanRepository;
    this.bookCopyRepository = bookCopyRepository;
    this.reservationRepository = reservationRepository;
  }

  @Override
  public ReservationId reserve(ReserveBook command) {
    var readerId = command.readerId();

    verifyReaderEligibility(readerId);

    var availableCopy = bookCopyRepository.findAvailableBookCopy(command.bookId());
    if (availableCopy.isEmpty()) {
      throw new NoAvailableBookCopyException(command.bookId());
    }

    var bookCopy = availableCopy.get();

    var reservation = Reservation.create(readerId, bookCopy.id());

    reservationRepository.create(reservation);
    bookCopyRepository.update(bookCopy);

    return reservation.id();
  }
}
