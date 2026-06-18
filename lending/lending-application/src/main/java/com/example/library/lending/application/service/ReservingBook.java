package com.example.library.lending.application.service;

import com.example.library.lending.application.command.ReserveBook;
import com.example.library.lending.application.port.in.IReserveBook;
import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.lending.application.repository.ReaderRepository;
import com.example.library.lending.application.repository.ReservationRepository;
import com.example.library.lending.domain.exception.NoAvailableBookCopyException;
import com.example.library.lending.domain.reservation.ReservationFactory;
import com.example.library.sharedkernel.identifier.ReservationId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;

public class ReservingBook implements IReserveBook {

  private final ReaderRepository readerRepository;
  private final BookCopyRepository bookCopyRepository;
  private final ReservationFactory reservationFactory;
  private final ReservationRepository reservationRepository;
  private final DomainEventPublisher eventPublisher;

  public ReservingBook(
      ReaderRepository readerRepository,
      BookCopyRepository bookCopyRepository,
      ReservationFactory reservationFactory,
      ReservationRepository reservationRepository,
      DomainEventPublisher eventPublisher) {
    this.readerRepository = readerRepository;
    this.bookCopyRepository = bookCopyRepository;
    this.reservationFactory = reservationFactory;
    this.reservationRepository = reservationRepository;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public ReservationId reserveBook(ReserveBook command) {
    // 2. System sprawdza konto Czytelnika (brak blokad, nieprzekroczony limit).
    var readerId = command.readerId();
    var reader =
        readerRepository
            .find(readerId)
            .orElseThrow(() -> new IllegalArgumentException("Reader not found: " + readerId));
    reader.verifyReservationEligibility();

    // 3. System wyszukuje egzemplarz tej książki o statusie „Dostępny”.
    var bookCopy =
        bookCopyRepository
            .findAvailableBookCopy(command.bookId())
            .orElseThrow(() -> new NoAvailableBookCopyException(command.bookId()));

    // 4. Utworzenie nowej Rezerwacji przypisanej do konta użytkownika..
    var bookCopyId = bookCopy.id();
    var reservation = reservationFactory.create(readerId, bookCopyId);
    var reservationId = reservation.id();
    reservationRepository.create(reservation);

    // 5. Status wybranego Egzemplarza jest ustawiany na „Zarezerwowany”.
    bookCopy.reserve(reservationId, readerId);
    bookCopyRepository.update(bookCopy);

    bookCopy.pullDomainEvents().forEach(eventPublisher::publish);

    return reservationId;
  }
}
