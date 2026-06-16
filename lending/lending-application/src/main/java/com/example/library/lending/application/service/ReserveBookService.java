package com.example.library.lending.application.service;

import com.example.library.lending.application.command.ReserveBook;
import com.example.library.lending.application.port.in.IReserveBook;
import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.lending.application.repository.LoanRepository;
import com.example.library.lending.application.repository.ReaderRepository;
import com.example.library.lending.application.repository.ReservationRepository;
import com.example.library.lending.domain.exception.LoanLimitExceededException;
import com.example.library.lending.domain.exception.NoAvailableBookCopyException;
import com.example.library.lending.domain.exception.ReaderBlockedException;
import com.example.library.lending.domain.reservation.ReservationFactory;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.identifier.ReservationId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;

public class ReserveBookService implements IReserveBook {

  private final ReaderRepository readerRepository;
  private final LoanRepository loanRepository;
  private final BookCopyRepository bookCopyRepository;
  private final ReservationFactory reservationFactory;
  private final ReservationRepository reservationRepository;
  private final DomainEventPublisher eventPublisher;

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
      ReservationFactory reservationFactory,
      ReservationRepository reservationRepository,
      DomainEventPublisher eventPublisher) {
    this.readerRepository = readerRepository;
    this.loanRepository = loanRepository;
    this.bookCopyRepository = bookCopyRepository;
    this.reservationFactory = reservationFactory;
    this.reservationRepository = reservationRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public ReservationId reserve(ReserveBook command) {
    var readerId = command.readerId();

    verifyReaderEligibility(readerId);

    var bookCopy =
        bookCopyRepository
            .findAvailableBookCopy(command.bookId())
            .orElseThrow(() -> new NoAvailableBookCopyException(command.bookId()));
    var bookCopyId = bookCopy.id();

    var reservation = reservationFactory.create(readerId, bookCopyId);
    var reservationId = reservation.id();

    bookCopy.reserve(reservationId, readerId);

    reservationRepository.create(reservation);
    bookCopyRepository.update(bookCopy);

    bookCopy.pullDomainEvents().forEach(eventPublisher::publish);

    return reservationId;
  }
}
