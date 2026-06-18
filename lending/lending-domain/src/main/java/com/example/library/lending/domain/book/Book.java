package com.example.library.lending.domain.book;

import com.example.library.lending.domain.exception.BookAlreadyInReaderWaitingQueueException;
import com.example.library.lending.domain.exception.ExtensionNotAllowedException;
import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.event.ReaderDequeued;
import com.example.library.sharedkernel.event.ReaderQueued;
import com.example.library.sharedkernel.identifier.BookId;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Deque;
import java.util.List;
import java.util.Optional;

public class Book extends AggregateRoot<BookId> {

  private final Deque<ReaderId> waitingQueue = new ArrayDeque<>();

  private Book(BookId id, Collection<ReaderId> waitingQueue) {
    super(id);
    this.waitingQueue.addAll(waitingQueue);
  }

  static Book of(BookId id, Collection<ReaderId> waitingQueue) {
    return new Book(id, waitingQueue);
  }

  public void verifyIfCopyCanBeExtended() {
    if (!waitingQueue.isEmpty()) {
      throw new ExtensionNotAllowedException(
          "Cannot extend the loan because there are readers in the queue for book: "
              + this.id()
              + ".");
    }
  }

  public void checkIfCanBeQueued(ReaderId readerId) {
    if (waitingQueue.contains(readerId)) {
      throw new BookAlreadyInReaderWaitingQueueException(readerId, this.id());
    }
  }

  public void addToQueue(ReaderId readerId) {
    waitingQueue.addLast(readerId);
    registerEvent(new ReaderQueued(this.id(), readerId));
  }

  public Optional<ReaderId> removeNextReaderFromQueue() {
    Optional<ReaderId> nextReader = Optional.ofNullable(waitingQueue.pollFirst());
    nextReader.ifPresent(readerId -> registerEvent(new ReaderDequeued(this.id(), readerId)));
    return nextReader;
  }

  public List<ReaderId> waitingQueue() {
    return List.copyOf(waitingQueue);
  }
}
