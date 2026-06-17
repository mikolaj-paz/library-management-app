package com.example.library.lending.application.repository;

import com.example.library.lending.application.port.out.ReaderPersistencePort;
import com.example.library.lending.domain.reader.Reader;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.Optional;

public class ReaderRepository {

  private final ReaderPersistencePort persistencePort;

  public ReaderRepository(ReaderPersistencePort persistencePort) {
    this.persistencePort = persistencePort;
  }

  public Optional<Reader> find(ReaderId id) {
    return persistencePort.find(id);
  }

  public void update(Reader reader) {
    persistencePort.update(reader);
  }
}
