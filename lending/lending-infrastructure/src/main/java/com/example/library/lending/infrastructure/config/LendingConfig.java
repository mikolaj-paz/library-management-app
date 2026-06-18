package com.example.library.lending.infrastructure.config;

import com.example.library.lending.application.port.in.IExpireReservations;
import com.example.library.lending.application.port.in.IExtendLoan;
import com.example.library.lending.application.port.in.IHandleOverdueBookReturn;
import com.example.library.lending.application.port.in.IHandleReservationExpired;
import com.example.library.lending.application.port.in.IJoinWaitingQueue;
import com.example.library.lending.application.port.in.ILendBookCopy;
import com.example.library.lending.application.port.in.IReserveBookCopy;
import com.example.library.lending.application.port.in.IReturnBookCopy;
import com.example.library.lending.application.port.in.IShowLoans;
import com.example.library.lending.application.port.out.BookCopyPersistencePort;
import com.example.library.lending.application.port.out.BookPersistencePort;
import com.example.library.lending.application.port.out.LoanPersistencePort;
import com.example.library.lending.application.port.out.ReaderPersistencePort;
import com.example.library.lending.application.port.out.ReservationPersistencePort;
import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.lending.application.repository.BookRepository;
import com.example.library.lending.application.repository.LoanRepository;
import com.example.library.lending.application.repository.ReaderRepository;
import com.example.library.lending.application.repository.ReservationRepository;
import com.example.library.lending.application.service.ExpiringReservations;
import com.example.library.lending.application.service.ExtendingLoan;
import com.example.library.lending.application.service.HandlingOverdueBookReturn;
import com.example.library.lending.application.service.HandlingReservationExpired;
import com.example.library.lending.application.service.JoiningWaitingQueue;
import com.example.library.lending.application.service.LendingBookCopy;
import com.example.library.lending.application.service.ReservingBookCopy;
import com.example.library.lending.application.service.ReturningBookCopy;
import com.example.library.lending.application.service.ShowingLoans;
import com.example.library.lending.domain.book.BookFactory;
import com.example.library.lending.domain.book.BookFactoryImpl;
import com.example.library.lending.domain.copy.BookCopyFactory;
import com.example.library.lending.domain.copy.BookCopyFactoryImpl;
import com.example.library.lending.domain.loan.LoanFactory;
import com.example.library.lending.domain.loan.LoanFactoryImpl;
import com.example.library.lending.domain.reader.ReaderFactory;
import com.example.library.lending.domain.reader.ReaderFactoryImpl;
import com.example.library.lending.domain.reservation.ReservationFactory;
import com.example.library.lending.domain.reservation.ReservationFactoryImpl;
import com.example.library.lending.infrastructure.in.events.BookCopyReturnOverdueListener;
import com.example.library.lending.infrastructure.in.events.FreeBookCopyOnReservationExpirationListener;
import com.example.library.lending.infrastructure.out.DomainEventPublisherImpl;
import com.example.library.lending.infrastructure.out.persistence.JdbcBookCopyRepository;
import com.example.library.lending.infrastructure.out.persistence.JdbcBookRepository;
import com.example.library.lending.infrastructure.out.persistence.JdbcLoanRepository;
import com.example.library.lending.infrastructure.out.persistence.JdbcReaderRepository;
import com.example.library.lending.infrastructure.out.persistence.JdbcReservationRepository;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class LendingConfig {

  @Bean
  DomainEventPublisher lendingDomainEventPublisher(ApplicationEventPublisher springPublisher) {
    return new DomainEventPublisherImpl(springPublisher);
  }

  @Bean
  LoanFactory lendingLoanFactory() {
    return new LoanFactoryImpl();
  }

  @Bean
  LoanPersistencePort lendingLoanPersistencePort(
      JdbcTemplate jdbc, LoanFactory lendingLoanFactory) {
    return new JdbcLoanRepository(jdbc, lendingLoanFactory);
  }

  @Bean
  BookFactory lendingBookFactory() {
    return new BookFactoryImpl();
  }

  @Bean
  BookPersistencePort lendingBookPersistencePort(
      JdbcTemplate jdbc, BookFactory lendingBookFactory) {
    return new JdbcBookRepository(jdbc, lendingBookFactory);
  }

  @Bean
  BookCopyFactory lendingBookCopyFactory() {
    return new BookCopyFactoryImpl();
  }

  @Bean
  BookCopyPersistencePort lendingBookCopyPersistencePort(
      JdbcTemplate jdbc, BookCopyFactory lendingBookCopyFactory) {
    return new JdbcBookCopyRepository(jdbc, lendingBookCopyFactory);
  }

  @Bean
  ReaderFactory lendingReaderFactory() {
    return new ReaderFactoryImpl();
  }

  @Bean
  ReaderPersistencePort lendingReaderPersistencePort(
      JdbcTemplate jdbc, ReaderFactory lendingReaderFactory) {
    return new JdbcReaderRepository(jdbc, lendingReaderFactory);
  }

  @Bean
  ReservationPersistencePort lendingReservationPersistencePort(
      JdbcTemplate jdbc, ReservationFactory lendingReservationFactory) {
    return new JdbcReservationRepository(jdbc, lendingReservationFactory);
  }

  @Bean
  LoanRepository lendingLoanRepository(LoanPersistencePort lendingLoanPersistencePort) {
    return new LoanRepository(lendingLoanPersistencePort);
  }

  @Bean
  BookRepository lendingBookRepository(BookPersistencePort lendingBookPersistencePort) {
    return new BookRepository(lendingBookPersistencePort);
  }

  @Bean
  BookCopyRepository lendingBookCopyRepository(
      BookCopyPersistencePort lendingBookCopyPersistencePort) {
    return new BookCopyRepository(lendingBookCopyPersistencePort);
  }

  @Bean
  ReaderRepository lendingReaderRepository(ReaderPersistencePort lendingReaderPersistencePort) {
    return new ReaderRepository(lendingReaderPersistencePort);
  }

  @Bean
  ReservationRepository lendingReservationRepository(
      ReservationPersistencePort lendingReservationPersistencePort) {
    return new ReservationRepository(lendingReservationPersistencePort);
  }

  @Bean
  IHandleOverdueBookReturn handlingOverdueBookReturn(ReaderRepository lendingReaderRepository) {
    return new HandlingOverdueBookReturn(lendingReaderRepository);
  }

  @Bean
  IHandleReservationExpired handlingReservationExpired(
      BookCopyRepository lendingBookCopyRepository) {
    return new HandlingReservationExpired(lendingBookCopyRepository);
  }

  @Bean
  BookCopyReturnOverdueListener bookCopyReturnOverdueListener(
      IHandleOverdueBookReturn handlingOverdueBookReturn) {
    return new BookCopyReturnOverdueListener(handlingOverdueBookReturn);
  }

  @Bean
  FreeBookCopyOnReservationExpirationListener freeBookCopyOnReservationExpirationListener(
      IHandleReservationExpired handlingReservationExpired) {
    return new FreeBookCopyOnReservationExpirationListener(handlingReservationExpired);
  }

  @Bean
  ILendBookCopy lendingBookCopy(
      LoanRepository lendingLoanRepository,
      BookCopyRepository lendingBookCopyRepository,
      ReaderRepository lendingReaderRepository,
      LoanFactory lendingLoanFactory,
      @Qualifier("lendingDomainEventPublisher") DomainEventPublisher lendingDomainEventPublisher) {
    return new LendingBookCopy(
        lendingLoanRepository,
        lendingBookCopyRepository,
        lendingReaderRepository,
        lendingLoanFactory,
        lendingDomainEventPublisher);
  }

  @Bean
  ReservationFactory lendingReservationFactory() {
    return new ReservationFactoryImpl();
  }

  @Bean
  IReserveBookCopy reservingBookCopy(
      ReaderRepository lendingReaderRepository,
      BookCopyRepository lendingBookCopyRepository,
      ReservationRepository lendingReservationRepository,
      ReservationFactory lendingReservationFactory,
      @Qualifier("lendingDomainEventPublisher") DomainEventPublisher lendingDomainEventPublisher) {
    return new ReservingBookCopy(
        lendingReaderRepository,
        lendingBookCopyRepository,
        lendingReservationFactory,
        lendingReservationRepository,
        lendingDomainEventPublisher);
  }

  @Bean
  IReturnBookCopy returningBookCopy(
      LoanRepository lendingLoanRepository,
      BookCopyRepository lendingBookCopyRepository,
      @Qualifier("lendingDomainEventPublisher") DomainEventPublisher lendingDomainEventPublisher) {
    return new ReturningBookCopy(
        lendingLoanRepository, lendingBookCopyRepository, lendingDomainEventPublisher);
  }

  @Bean
  IExtendLoan extendingLoan(
      LoanRepository lendingLoanRepository,
      BookRepository lendingBookRepository,
      BookCopyRepository lendingBookCopyRepository,
      @Qualifier("lendingDomainEventPublisher") DomainEventPublisher lendingDomainEventPublisher) {
    return new ExtendingLoan(
        lendingLoanRepository,
        lendingBookRepository,
        lendingBookCopyRepository,
        lendingDomainEventPublisher);
  }

  @Bean
  IShowLoans showingLoans(LoanRepository lendingLoanRepository) {
    return new ShowingLoans(lendingLoanRepository);
  }

  @Bean
  IJoinWaitingQueue joiningWaitingQueue(
      BookRepository lendingBookRepository,
      @Qualifier("lendingDomainEventPublisher") DomainEventPublisher lendingDomainEventPublisher) {
    return new JoiningWaitingQueue(lendingBookRepository, lendingDomainEventPublisher);
  }

  @Bean
  IExpireReservations expiringReservations(
      ReservationRepository lendingReservationRepository,
      @Qualifier("lendingDomainEventPublisher") DomainEventPublisher lendingDomainEventPublisher) {
    return new ExpiringReservations(lendingReservationRepository, lendingDomainEventPublisher);
  }
}
