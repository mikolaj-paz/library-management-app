package com.example.library.lending.infrastructure.config;

import com.example.library.lending.application.port.in.IExtendLoan;
import com.example.library.lending.application.port.in.ILendBookCopy;
import com.example.library.lending.application.port.in.IReserveBook;
import com.example.library.lending.application.port.in.IReturnBookCopy;
import com.example.library.lending.application.port.in.IShowLoans;
import com.example.library.lending.application.port.out.BookCopyRepository;
import com.example.library.lending.application.port.out.BookRepository;
import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.application.port.out.ReaderRepository;
import com.example.library.lending.application.port.out.ReservationRepository;
import com.example.library.lending.application.service.ExtendLoanService;
import com.example.library.lending.application.service.LendBookCopyService;
import com.example.library.lending.application.service.ReserveBookService;
import com.example.library.lending.application.service.ReturnBookCopyService;
import com.example.library.lending.application.service.ShowLoansService;
import com.example.library.lending.domain.copy.BookCopyFactory;
import com.example.library.lending.domain.copy.BookCopyFactoryImpl;
import com.example.library.lending.domain.loan.LoanFactory;
import com.example.library.lending.domain.loan.LoanFactoryImpl;
import com.example.library.lending.domain.reader.ReaderFactory;
import com.example.library.lending.domain.reader.ReaderFactoryImpl;
import com.example.library.lending.domain.reservation.ReservationFactory;
import com.example.library.lending.domain.reservation.ReservationFactoryImpl;
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
  LoanRepository lendingLoanRepository(JdbcTemplate jdbc, LoanFactory lendingLoanFactory) {
    return new JdbcLoanRepository(jdbc, lendingLoanFactory);
  }

  @Bean
  BookRepository lendingBookRepository(JdbcTemplate jdbc) {
    return new JdbcBookRepository(jdbc);
  }

  @Bean
  BookCopyFactory lendingBookCopyFactory() {
    return new BookCopyFactoryImpl();
  }

  @Bean
  BookCopyRepository lendingBookCopyRepository(
      JdbcTemplate jdbc, BookCopyFactory lendingBookCopyFactory) {
    return new JdbcBookCopyRepository(jdbc, lendingBookCopyFactory);
  }

  @Bean
  ReaderFactory lendingReaderFactory() {
    return new ReaderFactoryImpl();
  }

  @Bean
  ReaderRepository lendingReaderRepository(JdbcTemplate jdbc, ReaderFactory lendingReaderFactory) {
    return new JdbcReaderRepository(jdbc, lendingReaderFactory);
  }

  @Bean
  ReservationRepository lendingReservationRepository(JdbcTemplate jdbc) {
    return new JdbcReservationRepository(jdbc);
  }

  @Bean
  ILendBookCopy lendBookCopy(
      LoanRepository lendingLoanRepository,
      BookCopyRepository lendingBookCopyRepository,
      ReaderRepository lendingReaderRepository,
      LoanFactory lendingLoanFactory,
      @Qualifier("lendingDomainEventPublisher") DomainEventPublisher lendingDomainEventPublisher) {
    return new LendBookCopyService(
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
  IReserveBook reserveBook(
      ReaderRepository lendingReaderRepository,
      LoanRepository lendingLoanRepository,
      BookCopyRepository lendingBookCopyRepository,
      ReservationRepository lendingReservationRepository,
      ReservationFactory lendingReservationFactory,
      @Qualifier("lendingDomainEventPublisher") DomainEventPublisher lendingDomainEventPublisher) {
    return new ReserveBookService(
        lendingReaderRepository,
        lendingLoanRepository,
        lendingBookCopyRepository,
        lendingReservationFactory,
        lendingReservationRepository,
        lendingDomainEventPublisher);
  }

  @Bean
  IReturnBookCopy returnBookCopy(
      LoanRepository lendingLoanRepository,
      BookCopyRepository lendingBookCopyRepository,
      @Qualifier("lendingDomainEventPublisher") DomainEventPublisher lendingDomainEventPublisher) {
    return new ReturnBookCopyService(
        lendingLoanRepository, lendingBookCopyRepository, lendingDomainEventPublisher);
  }

  @Bean
  IExtendLoan extendLoan(
      LoanRepository lendingLoanRepository,
      BookRepository lendingBookRepository,
      BookCopyRepository lendingBookCopyRepository,
      @Qualifier("lendingDomainEventPublisher") DomainEventPublisher lendingDomainEventPublisher) {
    return new ExtendLoanService(
        lendingLoanRepository,
        lendingBookRepository,
        lendingBookCopyRepository,
        lendingDomainEventPublisher);
  }

  @Bean
  IShowLoans showLoans(LoanRepository lendingLoanRepository) {
    return new ShowLoansService(lendingLoanRepository);
  }
}
