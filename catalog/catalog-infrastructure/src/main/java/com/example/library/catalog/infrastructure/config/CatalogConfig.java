package com.example.library.catalog.infrastructure.config;

import com.example.library.catalog.application.port.in.IAddBook;
import com.example.library.catalog.application.port.in.IAddBookCopy;
import com.example.library.catalog.application.port.in.IGetBookDetails;
import com.example.library.catalog.application.port.in.IRemoveBookCopy;
import com.example.library.catalog.application.port.in.ISearchCatalog;
import com.example.library.catalog.application.port.out.BookCopyPersistencePort;
import com.example.library.catalog.application.port.out.BookPersistencePort;
import com.example.library.catalog.application.port.out.CatalogQueryPort;
import com.example.library.catalog.application.repository.BookCopyRepository;
import com.example.library.catalog.application.repository.BookRepository;
import com.example.library.catalog.application.service.AddBookCopyService;
import com.example.library.catalog.application.service.AddingBook;
import com.example.library.catalog.application.service.GetBookDetailsService;
import com.example.library.catalog.application.service.RemoveBookCopyService;
import com.example.library.catalog.application.service.SearchCatalogService;
import com.example.library.catalog.domain.book.BookFactory;
import com.example.library.catalog.domain.book.BookFactoryImpl;
import com.example.library.catalog.domain.copy.BookCopyFactory;
import com.example.library.catalog.domain.copy.BookCopyFactoryImpl;
import com.example.library.catalog.infrastructure.out.DomainEventPublisherImpl;
import com.example.library.catalog.infrastructure.out.persistence.JdbcBookCopyRepository;
import com.example.library.catalog.infrastructure.out.persistence.JdbcBookRepository;
import com.example.library.catalog.infrastructure.out.persistence.JdbcCatalogQueryPort;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class CatalogConfig {

  @Bean
  BookCopyFactory catalogBookCopyFactory() {
    return new BookCopyFactoryImpl();
  }

  @Bean
  BookFactory catalogBookFactory() {
    return new BookFactoryImpl();
  }

  @Bean
  DomainEventPublisher catalogDomainEventPublisher(ApplicationEventPublisher springPublisher) {
    return new DomainEventPublisherImpl(springPublisher);
  }

  @Bean
  CatalogQueryPort catalogQueryPort(JdbcTemplate jdbc) {
    return new JdbcCatalogQueryPort(jdbc);
  }

  @Bean
  BookCopyPersistencePort catalogBookCopyPersistencePort(
      JdbcTemplate jdbc, BookCopyFactory catalogBookCopyFactory) {
    return new JdbcBookCopyRepository(jdbc, catalogBookCopyFactory);
  }

  @Bean
  BookPersistencePort catalogBookPersistencePort(
      JdbcTemplate jdbc, BookFactory catalogBookFactory) {
    return new JdbcBookRepository(jdbc, catalogBookFactory);
  }

  @Bean
  BookCopyRepository catalogBookCopyRepository(
      BookCopyPersistencePort catalogBookCopyPersistencePort) {
    return new BookCopyRepository(catalogBookCopyPersistencePort);
  }

  @Bean
  BookRepository catalogBookRepository(BookPersistencePort catalogBookPersistencePort) {
    return new BookRepository(catalogBookPersistencePort);
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
      BookCopyFactory catalogBookCopyFactory,
      BookCopyRepository catalogBookCopyRepository,
      BookRepository catalogBookRepository,
      @Qualifier("catalogDomainEventPublisher") DomainEventPublisher catalogDomainEventPublisher) {
    return new AddBookCopyService(
        catalogBookCopyFactory,
        catalogBookCopyRepository,
        catalogBookRepository,
        catalogDomainEventPublisher);
  }

  @Bean
  IRemoveBookCopy removeBookCopy(
      BookCopyRepository catalogBookCopyRepository,
      @Qualifier("catalogDomainEventPublisher") DomainEventPublisher catalogDomainEventPublisher) {
    return new RemoveBookCopyService(catalogBookCopyRepository, catalogDomainEventPublisher);
  }

  @Bean
  IAddBook addBook(
      BookFactory catalogBookFactory,
      BookRepository catalogBookRepository,
      @Qualifier("catalogDomainEventPublisher") DomainEventPublisher catalogDomainEventPublisher) {
    return new AddingBook(catalogBookRepository, catalogBookFactory);
  }
}
