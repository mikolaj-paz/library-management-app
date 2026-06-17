package com.example.library.lending.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.lending.application.repository.ReaderRepository;
import com.example.library.lending.domain.reader.ReaderFactoryImpl;
import com.example.library.lending.domain.reader.ReaderStatus;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HandlingOverdueBookReturnTest {

  @Mock private ReaderRepository readerRepository;

  @Test
  void should_block_reader_after_overdue_book_return() {
    var readerId = ReaderId.create();
    var reader = new ReaderFactoryImpl().reconstitute(readerId, ReaderStatus.ACTIVE, 0);
    when(readerRepository.find(readerId)).thenReturn(Optional.of(reader));
    var service = new HandlingOverdueBookReturn(readerRepository);

    service.handleOverdueBookReturn(readerId);

    assertThat(reader.status()).isEqualTo(ReaderStatus.BLOCKED);
    verify(readerRepository).update(reader);
  }
}
