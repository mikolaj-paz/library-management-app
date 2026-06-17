package com.example.library.lending.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.library.lending.application.command.JoinWaitingQueue;
import com.example.library.lending.application.repository.BookRepository;
import com.example.library.lending.domain.book.Book;
import com.example.library.lending.domain.book.BookFactoryImpl;
import com.example.library.lending.domain.exception.BookAlreadyInReaderWaitingQueueException;
import com.example.library.sharedkernel.event.DomainEvent;
import com.example.library.sharedkernel.event.ReaderQueued;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import com.example.library.sharedkernel.publisher.DomainEventPublisher;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JoiningWaitingQueueTest {

  @Mock private BookRepository bookRepository;

  @Mock private DomainEventPublisher eventPublisher;

  @Test
  void should_add_reader_to_waiting_queue_and_publish_event() {
    var bookId = BookId.create();
    var readerId = ReaderId.create();
    var book = new BookFactoryImpl().reconstitute(bookId, List.of());
    when(bookRepository.find(bookId)).thenReturn(Optional.of(book));
    var service = new JoiningWaitingQueue(bookRepository, eventPublisher);

    service.joinWaitingQueue(new JoinWaitingQueue(readerId, bookId));

    assertThat(book.waitingQueue()).containsExactly(readerId);
    verify(bookRepository).update(book);
    var eventCaptor = ArgumentCaptor.forClass(DomainEvent.class);
    verify(eventPublisher).publish(eventCaptor.capture());
    assertThat(eventCaptor.getValue())
        .isInstanceOfSatisfying(
            ReaderQueued.class,
            event -> {
              assertThat(event.getBookId()).isEqualTo(bookId);
              assertThat(event.getReaderId()).isEqualTo(readerId);
            });
  }

  @Test
  void should_reject_reader_already_in_waiting_queue() {
    var bookId = BookId.create();
    var readerId = ReaderId.create();
    var book = new BookFactoryImpl().reconstitute(bookId, List.of(readerId));
    when(bookRepository.find(bookId)).thenReturn(Optional.of(book));
    var service = new JoiningWaitingQueue(bookRepository, eventPublisher);

    assertThatThrownBy(() -> service.joinWaitingQueue(new JoinWaitingQueue(readerId, bookId)))
        .isInstanceOf(BookAlreadyInReaderWaitingQueueException.class);

    verify(bookRepository, never()).update(any());
    verify(eventPublisher, never()).publish(any());
  }
}
