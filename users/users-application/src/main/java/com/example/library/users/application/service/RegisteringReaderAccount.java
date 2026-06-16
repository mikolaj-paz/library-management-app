package com.example.library.users.application.service;

import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import com.example.library.users.application.command.RegisterReaderAccount;
import com.example.library.users.application.port.in.IRegisterReaderAccount;
import com.example.library.users.application.repository.ReaderAcountRepository;
import com.example.library.users.domain.reader.ReaderAccount;
import com.example.library.users.domain.reader.ReaderAccountFactory;

public class RegisteringReaderAccount implements IRegisterReaderAccount {

  private final ReaderAcountRepository readerAccountRepository;
  private final ReaderAccountFactory readerAccountFactory;
  private final DomainEventPublisher eventPublisher;

  public RegisteringReaderAccount(
      ReaderAcountRepository readerAccountRepository,
      ReaderAccountFactory readerAccountFactory,
      DomainEventPublisher eventPublisher) {
    this.readerAccountRepository = readerAccountRepository;
    this.readerAccountFactory = readerAccountFactory;
    this.eventPublisher = eventPublisher;
  }

  @Override
  public ReaderAccount registerReaderAccount(RegisterReaderAccount command) {

    // 2. System weryfikuje w bazie danych na podstawie adresu e-mail, czy taki użytkownik już nie
    // istnieje.
    readerAccountRepository
        .findByEmail(command.email())
        .ifPresent(
            readerAccount -> {
              throw new IllegalArgumentException(
                  "Reader account with email " + command.email() + " already exists.");
            });

    // 3. Tworzenie nowego obiektu Konta Czytelnika i automatyczne wygenerowanie dla niego
    // unikalnego hasła startowego.
    // 4. Ustawienie statusu konta bezpośrednio na „Aktywne”
    var readerAccount =
        readerAccountFactory.create(
            command.name(), command.surname(), command.email(), command.telephone());

    // 5. Zapisanie danych konta w bazie danych użytkowników.
    readerAccountRepository.create(readerAccount);

    readerAccount.pullDomainEvents().forEach(eventPublisher::publish);

    return readerAccount;
  }
}
