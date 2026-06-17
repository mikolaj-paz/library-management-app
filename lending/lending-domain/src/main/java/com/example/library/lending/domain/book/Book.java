package com.example.library.lending.domain.book;

import com.example.library.sharedkernel.entity.AggregateRoot;
import com.example.library.sharedkernel.event.ReaderDequeuedEvent;
import com.example.library.sharedkernel.event.ReaderQueuedEvent;
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

  public boolean hasQueuedReader() {
    return !waitingQueue.isEmpty();
  }

  public boolean hasQueued(ReaderId readerId) {
    return waitingQueue.contains(readerId);
  }

  public void addToQueue(ReaderId readerId) {
    waitingQueue.addLast(readerId);
    registerEvent(new ReaderQueuedEvent(this.id(), readerId));
  }

  public Optional<ReaderId> removeNextReaderFromQueue() {
    Optional<ReaderId> nextReader = Optional.ofNullable(waitingQueue.pollFirst());
    nextReader.ifPresent(readerId -> registerEvent(new ReaderDequeuedEvent(this.id(), readerId)));
    return nextReader;
  }

  public List<ReaderId> waitingQueue() {
    return List.copyOf(waitingQueue);
  }
}
