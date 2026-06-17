package com.example.library.lending.infrastructure.in.events;

import com.example.library.lending.application.repository.ReaderRepository;
import com.example.library.sharedkernel.event.BookCopyReturned;
import org.springframework.context.event.EventListener;

public class BookCopyReturnOverdueListener {

  private final ReaderRepository readerRepository;

  public BookCopyReturnOverdueListener(ReaderRepository readerRepository) {
    this.readerRepository = readerRepository;
  }

  @EventListener
  public void on(BookCopyReturned event) {
    if (!event.isOverdue()) return;

    var reader =
        readerRepository
            .find(event.readerId())
            .orElseThrow(() -> new IllegalStateException("Reader not found: " + event.readerId()));

    reader.block();

    readerRepository.update(reader);
  }
}
