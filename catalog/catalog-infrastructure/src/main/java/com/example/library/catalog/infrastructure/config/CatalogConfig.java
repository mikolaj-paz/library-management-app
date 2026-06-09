package com.example.library.catalog.infrastructure.config;

import com.example.library.catalog.application.port.in.IAddBookCopy;
import com.example.library.catalog.application.port.in.IGetBookDetails;
import com.example.library.catalog.application.port.in.IRemoveBookCopy;
import com.example.library.catalog.application.port.in.ISearchCatalog;
import com.example.library.catalog.application.port.out.BookCopyRepository;
import com.example.library.catalog.application.port.out.BookRepository;
import com.example.library.catalog.application.port.out.CatalogQueryPort;
import com.example.library.catalog.application.service.AddBookCopyService;
import com.example.library.catalog.application.service.GetBookDetailsService;
import com.example.library.catalog.application.service.RemoveBookCopyService;
import com.example.library.catalog.application.service.SearchCatalogService;
import com.example.library.catalog.infrastructure.out.DomainEventPublisherImpl;
import com.example.library.catalog.infrastructure.out.persistence.JdbcBookCopyRepository;
import com.example.library.catalog.infrastructure.out.persistence.JdbcBookRepository;
import com.example.library.catalog.infrastructure.out.persistence.JdbcCatalogQueryPort;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class CatalogConfig {

  @Bean
  DomainEventPublisher domainEventPublisher(ApplicationEventPublisher springPublisher) {
    return new DomainEventPublisherImpl(springPublisher);
  }

  @Bean
  CatalogQueryPort catalogQueryPort(JdbcTemplate jdbc) {
    return new JdbcCatalogQueryPort(jdbc);
  }

  @Bean
  BookCopyRepository bookCopyRepository(JdbcTemplate jdbc) {
    return new JdbcBookCopyRepository(jdbc);
  }

  @Bean
  BookRepository bookRepository(JdbcTemplate jdbc) {
    return new JdbcBookRepository(jdbc);
  }

  @Bean
  ISearchCatalog searchCatalog(CatalogQueryPort catalogQueryPort) {
    return new SearchCatalogService(catalogQueryPort);
  }

  @Bean
  IGetBookDetails getBookDetails(CatalogQueryPort catalogQueryPort) {
    return new GetBookDetailsService(catalogQueryPort);
  }

  @Bean
  IAddBookCopy addBookCopy(
      BookCopyRepository bookCopyRepository,
      BookRepository bookRepository,
      DomainEventPublisher domainEventPublisher) {
    return new AddBookCopyService(bookCopyRepository, bookRepository, domainEventPublisher);
  }

  @Bean
  IRemoveBookCopy removeBookCopy(
      BookCopyRepository bookCopyRepository, DomainEventPublisher domainEventPublisher) {
    return new RemoveBookCopyService(bookCopyRepository, domainEventPublisher);
  }
}
