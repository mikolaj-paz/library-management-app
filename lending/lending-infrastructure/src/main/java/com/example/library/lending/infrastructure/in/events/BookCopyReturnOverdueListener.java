package com.example.library.lending.infrastructure.in.events;

import com.example.library.lending.application.port.in.IHandleOverdueBookReturn;
import com.example.library.sharedkernel.event.BookCopyReturned;
import org.springframework.context.event.EventListener;

public class BookCopyReturnOverdueListener {

  private final IHandleOverdueBookReturn handleOverdueBookReturn;

  public BookCopyReturnOverdueListener(IHandleOverdueBookReturn handleOverdueBookReturn) {
    this.handleOverdueBookReturn = handleOverdueBookReturn;
  }

  @EventListener
  public void on(BookCopyReturned event) {
    if (!event.isOverdue()) return;

    handleOverdueBookReturn.handleOverdueBookReturn(event.readerId());
  }
}
