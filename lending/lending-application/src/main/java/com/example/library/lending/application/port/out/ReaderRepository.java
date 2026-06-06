package com.example.library.lending.application.port.out;

import com.example.library.lending.domain.reader.Reader;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.Optional;

public interface ReaderRepository {
  Optional<Reader> findById(ReaderId id);
}
