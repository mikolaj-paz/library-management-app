package com.example.library.lending.domain.book;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.sharedkernel.event.ReaderDequeued;
import com.example.library.sharedkernel.event.ReaderQueued;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.List;
import org.junit.jupiter.api.Test;

class BookTest {

  @Test
  void should_add_reader_to_waiting_queue_and_register_event() {
    var bookId = BookId.create();
    var readerId = ReaderId.create();
    var book = new BookFactoryImpl().reconstitute(bookId, List.of());

    book.addToQueue(readerId);

    assertThat(book.waitingQueue()).containsExactly(readerId);
    assertThat(book.pullDomainEvents())
        .singleElement()
        .isInstanceOfSatisfying(
            ReaderQueued.class,
            event -> {
              assertThat(event.getBookId()).isEqualTo(bookId);
              assertThat(event.getReaderId()).isEqualTo(readerId);
            });
  }

  @Test
  void should_remove_next_reader_from_waiting_queue_and_register_event() {
    var bookId = BookId.create();
    var firstReaderId = ReaderId.create();
    var secondReaderId = ReaderId.create();
    var book = new BookFactoryImpl().reconstitute(bookId, List.of(firstReaderId, secondReaderId));

    var removedReaderId = book.removeNextReaderFromQueue();

    assertThat(removedReaderId).contains(firstReaderId);
    assertThat(book.waitingQueue()).containsExactly(secondReaderId);
    assertThat(book.pullDomainEvents())
        .singleElement()
        .isInstanceOfSatisfying(
            ReaderDequeued.class,
            event -> {
              assertThat(event.getBookId()).isEqualTo(bookId);
              assertThat(event.getReaderId()).isEqualTo(firstReaderId);
            });
  }
}
