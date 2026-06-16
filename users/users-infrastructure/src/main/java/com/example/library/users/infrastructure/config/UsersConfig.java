package com.example.library.users.infrastructure.config;

import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import com.example.library.users.application.port.in.IRegisterReaderAccount;
import com.example.library.users.application.port.out.ReaderAccountPersistancePort;
import com.example.library.users.application.repository.ReaderAcountRepository;
import com.example.library.users.application.service.RegisteringReaderAccount;
import com.example.library.users.domain.reader.ReaderAccountFactory;
import com.example.library.users.domain.reader.ReaderAccountFactoryImpl;
import com.example.library.users.infrastructure.out.DomainEventPublisherImpl;
import com.example.library.users.infrastructure.out.persistance.JdbcReaderAccountRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class UsersConfig {

  @Bean
  DomainEventPublisher usersDomainEventPublisher(ApplicationEventPublisher springPublisher) {
    return new DomainEventPublisherImpl(springPublisher);
  }

  @Bean
  ReaderAccountFactory usersReaderAccountFactory() {
    return new ReaderAccountFactoryImpl();
  }

  @Bean
  ReaderAccountPersistancePort usersReaderAccountPersistancePort(
      JdbcTemplate jdbc, ReaderAccountFactory usersReaderAccountFactory) {
    return new JdbcReaderAccountRepository(jdbc, usersReaderAccountFactory);
  }

  @Bean
  ReaderAcountRepository usersReaderAccountRepository(
      ReaderAccountPersistancePort usersReaderAccountPersistancePort) {
    return new ReaderAcountRepository(usersReaderAccountPersistancePort);
  }

  @Bean
  IRegisterReaderAccount registerReaderAccount(
      ReaderAccountFactory usersReaderAccountFactory,
      ReaderAcountRepository usersReaderAccountRepository,
      @Qualifier("usersDomainEventPublisher") DomainEventPublisher usersDomainEventPublisher) {
    return new RegisteringReaderAccount(
        usersReaderAccountRepository, usersReaderAccountFactory, usersDomainEventPublisher);
  }
}
