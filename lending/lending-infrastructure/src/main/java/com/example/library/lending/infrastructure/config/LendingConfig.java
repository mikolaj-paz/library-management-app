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
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class LendingConfig {

  @Bean
  DomainEventPublisher domainEventPublisher(ApplicationEventPublisher springPublisher) {
    return new DomainEventPublisherImpl(springPublisher);
  }

  @Bean
  LoanFactory loanFactory() {
    return new LoanFactoryImpl();
  }

  @Bean
  LoanRepository loanRepository(JdbcTemplate jdbc, LoanFactory loanFactory) {
    return new JdbcLoanRepository(jdbc, loanFactory);
  }

  @Bean
  BookRepository bookRepository(JdbcTemplate jdbc) {
    return new JdbcBookRepository(jdbc);
  }

  @Bean
  BookCopyFactory bookCopyFactory() {
    return new BookCopyFactoryImpl();
  }

  @Bean
  BookCopyRepository bookCopyRepository(JdbcTemplate jdbc, BookCopyFactory bookCopyFactory) {
    return new JdbcBookCopyRepository(jdbc, bookCopyFactory);
  }

  @Bean
  ReaderFactory readerFactory() {
    return new ReaderFactoryImpl();
  }

  @Bean
  ReaderRepository readerRepository(JdbcTemplate jdbc, ReaderFactory readerFactory) {
    return new JdbcReaderRepository(jdbc, readerFactory);
  }

  @Bean
  ReservationRepository reservationRepository(JdbcTemplate jdbc) {
    return new JdbcReservationRepository(jdbc);
  }

  @Bean
  ILendBookCopy lendBookCopy(
      LoanRepository loanRepository,
      BookCopyRepository bookCopyRepository,
      ReaderRepository readerRepository,
      LoanFactory loanFactory,
      DomainEventPublisher domainEventPublisher) {
    return new LendBookCopyService(
        loanRepository, bookCopyRepository, readerRepository, loanFactory, domainEventPublisher);
  }

  @Bean
  ReservationFactory reservationFactory() {
    return new ReservationFactoryImpl();
  }

  @Bean
  IReserveBook reserveBook(
      ReaderRepository readerRepository,
      LoanRepository loanRepository,
      BookCopyRepository bookCopyRepository,
      ReservationRepository reservationRepository,
      ReservationFactory reservationFactory,
      DomainEventPublisher domainEventPublisher) {
    return new ReserveBookService(
        readerRepository,
        loanRepository,
        bookCopyRepository,
        reservationFactory,
        reservationRepository,
        domainEventPublisher);
  }

  @Bean
  IReturnBookCopy returnBookCopy(
      LoanRepository loanRepository,
      BookCopyRepository bookCopyRepository,
      DomainEventPublisher domainEventPublisher) {
    return new ReturnBookCopyService(loanRepository, bookCopyRepository, domainEventPublisher);
  }

  @Bean
  IExtendLoan extendLoan(
      LoanRepository loanRepository,
      BookRepository bookRepository,
      BookCopyRepository bookCopyRepository,
      DomainEventPublisher domainEventPublisher) {
    return new ExtendLoanService(
        loanRepository, bookRepository, bookCopyRepository, domainEventPublisher);
  }

  @Bean
  IShowLoans showLoans(LoanRepository loanRepository) {
    return new ShowLoansService(loanRepository);
  }
}
