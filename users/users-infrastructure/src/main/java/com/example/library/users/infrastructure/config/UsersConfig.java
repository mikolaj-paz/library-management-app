package com.example.library.users.infrastructure.config;

import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import com.example.library.users.application.port.in.IRegisterReaderAccount;
import com.example.library.users.application.port.in.IUnblockReaderAccount;
import com.example.library.users.application.port.out.ReaderAccountPersistancePort;
import com.example.library.users.application.repository.ReaderAccountRepository;
import com.example.library.users.application.service.RegisteringReaderAccount;
import com.example.library.users.application.service.UnblockingUserAccount;
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
  ReaderAccountRepository usersReaderAccountRepository(
      ReaderAccountPersistancePort usersReaderAccountPersistancePort) {
    return new ReaderAccountRepository(usersReaderAccountPersistancePort);
  }

  @Bean
  IRegisterReaderAccount registerReaderAccount(
      ReaderAccountFactory usersReaderAccountFactory,
      ReaderAccountRepository usersReaderAccountRepository,
      @Qualifier("usersDomainEventPublisher") DomainEventPublisher usersDomainEventPublisher) {
    return new RegisteringReaderAccount(
        usersReaderAccountRepository, usersReaderAccountFactory, usersDomainEventPublisher);
  }

  @Bean
  IUnblockReaderAccount unblockReaderAccount(
      ReaderAccountRepository usersReaderAccountRepository,
      @Qualifier("usersDomainEventPublisher") DomainEventPublisher usersDomainEventPublisher) {
    return new UnblockingUserAccount(usersReaderAccountRepository, usersDomainEventPublisher);
  }
}
