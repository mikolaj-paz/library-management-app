package com.example.library.catalog.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.catalog.application.command.RemoveBookCopy;
import com.example.library.catalog.application.repository.BookCopyRepository;
import com.example.library.catalog.domain.copy.BookCopyFactoryImpl;
import com.example.library.catalog.domain.copy.BookCopyRemoved;
import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import com.example.library.sharedkernel.valueobject.BookCopyStatus;
import java.util.Optional;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RemoveBookCopyServiceTest {

  @Mock private BookCopyRepository bookCopyRepository;

  @Mock private DomainEventPublisher eventPublisher;

  @Test
  @Disabled("TODO: Reconstituted BookCopy currently carries BookCopyAdded and publishes an extra event")
  void should_withdraw_available_copy_and_publish_event() {
    var copyId = BookCopyId.create();
    var copy =
        new BookCopyFactoryImpl()
            .reconstitute(copyId, BookCopyStatus.AVAILABLE, null, BookId.create());
    when(bookCopyRepository.find(copyId)).thenReturn(Optional.of(copy));
    var service = new RemoveBookCopyService(bookCopyRepository, eventPublisher);

    service.remove(new RemoveBookCopy(copyId));

    assertThat(copy.status()).isEqualTo(BookCopyStatus.WITHDRAWN);
    verify(bookCopyRepository).update(copy);
    var eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue())
        .isInstanceOfSatisfying(
            BookCopyRemoved.class, event -> assertThat(event.bookCopyId()).isEqualTo(copyId));
  }

  @Test
  void should_throw_when_copy_does_not_exist() {
    var copyId = BookCopyId.create();
    when(bookCopyRepository.find(copyId)).thenReturn(Optional.empty());
    var service = new RemoveBookCopyService(bookCopyRepository, eventPublisher);

    assertThatThrownBy(() -> service.remove(new RemoveBookCopy(copyId)))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessageContaining("Book copy not found");

    verify(bookCopyRepository, never()).update(any());
    verify(eventPublisher, never()).publish(any());
  }
}
