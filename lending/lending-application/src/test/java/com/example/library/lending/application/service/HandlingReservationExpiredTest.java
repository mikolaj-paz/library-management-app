package com.example.library.lending.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.lending.application.repository.BookCopyRepository;
import com.example.library.lending.domain.copy.BookCopyFactoryImpl;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class HandlingReservationExpiredTest {

  @Mock private BookCopyRepository bookCopyRepository;

  @Test
  void should_mark_reserved_copy_available_when_reservation_expires() {
    var copyId = BookCopyId.create();
    var copy =
        new BookCopyFactoryImpl()
            .reconstitute(copyId, BookCopyStatus.RESERVED, ReaderId.create(), BookId.create());
    when(bookCopyRepository.find(copyId)).thenReturn(Optional.of(copy));
    var service = new HandlingReservationExpired(bookCopyRepository);

    service.handleReservationExpired(copyId);

    assertThat(copy.status()).isEqualTo(BookCopyStatus.AVAILABLE);
    assertThat(copy.reservedBy()).isNull();
    verify(bookCopyRepository).update(copy);
  }
}
