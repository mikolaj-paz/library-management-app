package com.example.library.lending.infrastructure.config;

import com.example.library.lending.application.port.in.ILendBookCopy;
import com.example.library.lending.application.port.out.BookCopyRepository;
import com.example.library.lending.application.port.out.LoanRepository;
import com.example.library.lending.application.port.out.ReaderRepository;
import com.example.library.lending.application.service.LendBookCopyService;
import com.example.library.lending.infrastructure.out.persistence.JdbcBookCopyRepository;
import com.example.library.lending.infrastructure.out.persistence.JdbcLoanRepository;
import com.example.library.lending.infrastructure.out.persistence.JdbcReaderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class LendingConfig {

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
  ILendBookCopy lendBookCopy(
      LoanRepository loanRepository,
      BookCopyRepository bookCopyRepository,
      ReaderRepository readerRepository) {
    return new LendBookCopyService(loanRepository, bookCopyRepository, readerRepository);
  }
}
