package com.example.library.lending.infrastructure.config;

import com.example.library.lending.application.port.in.ILendBookCopy;
import com.example.library.lending.application.port.in.IReserveBook;
import com.example.library.lending.application.port.out.BookCopyRepository;
import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.application.port.out.ReaderRepository;
import com.example.library.lending.application.port.out.ReservationRepository;
import com.example.library.lending.application.service.LendBookCopyService;
import com.example.library.lending.application.service.ReserveBookService;
import com.example.library.lending.infrastructure.out.DomainEventPublisherImpl;
import com.example.library.lending.infrastructure.out.persistence.JdbcBookCopyRepository;
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
  LoanRepository loanRepository(JdbcTemplate jdbc) {
    return new JdbcLoanRepository(jdbc);
  }

  @Bean
  BookCopyRepository bookCopyRepository(JdbcTemplate jdbc) {
    return new JdbcBookCopyRepository(jdbc);
  }

  @Bean
  ReaderRepository readerRepository(JdbcTemplate jdbc) {
    return new JdbcReaderRepository(jdbc);
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
      DomainEventPublisher eventPublisher) {
    return new LendBookCopyService(
        loanRepository, bookCopyRepository, readerRepository, eventPublisher);
  }

  @Bean
  IReserveBook reserveBook(
      ReaderRepository readerRepository,
      LoanRepository loanRepository,
      BookCopyRepository bookCopyRepository,
      ReservationRepository reservationRepository,
      DomainEventPublisher eventPublisher) {
    return new ReserveBookService(
        readerRepository,
        loanRepository,
        bookCopyRepository,
        reservationRepository,
        eventPublisher);
  }
}
