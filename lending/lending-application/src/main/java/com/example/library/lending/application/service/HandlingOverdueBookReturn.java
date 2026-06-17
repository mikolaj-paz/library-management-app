package com.example.library.lending.application.service;

import com.example.library.lending.application.port.in.IHandleOverdueBookReturn;
import com.example.library.lending.application.repository.ReaderRepository;
import com.example.library.sharedkernel.identifier.ReaderId;

public class HandlingOverdueBookReturn implements IHandleOverdueBookReturn {

  private final ReaderRepository readerRepository;

  public HandlingOverdueBookReturn(ReaderRepository readerRepository) {
    this.readerRepository = readerRepository;
  }

  @Override
  public void handleOverdueBookReturn(ReaderId readerId) {
    var reader =
        readerRepository
            .find(readerId)
            .orElseThrow(() -> new IllegalStateException("Reader not found: " + readerId));

    reader.block();
    readerRepository.update(reader);
  }
}
