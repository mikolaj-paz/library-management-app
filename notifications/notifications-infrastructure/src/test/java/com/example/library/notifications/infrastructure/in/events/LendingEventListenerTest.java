package com.example.library.notifications.infrastructure.in.events;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.example.library.notifications.application.command.InformCommand;
import com.example.library.notifications.application.port.in.IInform;
import com.example.library.notifications.domain.notification.NotificationType;
import com.example.library.sharedkernel.event.BookCopyLoaned;
import com.example.library.sharedkernel.event.BookCopyReturned;
import com.example.library.sharedkernel.identifier.BookCopyId;
import com.example.library.sharedkernel.identifier.LoanId;
import com.example.library.sharedkernel.identifier.ReaderId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LendingEventListenerTest {

  @Mock private IInform informService;

  @Test
  void should_inform_reader_when_book_copy_is_loaned() {
    var readerId = ReaderId.create();
    var bookCopyId = BookCopyId.create();
    var listener = new LendingEventListener(informService);

    listener.on(new BookCopyLoaned(LoanId.create(), readerId, bookCopyId));

    var commandCaptor = ArgumentCaptor.forClass(InformCommand.class);
    verify(informService).inform(commandCaptor.capture());
    assertThat(commandCaptor.getValue().readerId()).isEqualTo(readerId);
    assertThat(commandCaptor.getValue().type()).isEqualTo(NotificationType.BOOK_COPY_LOANED);
    assertThat(commandCaptor.getValue().message()).contains(bookCopyId.toString());
  }

  @Test
  void should_use_overdue_notification_type_when_return_is_overdue() {
    var readerId = ReaderId.create();
    var bookCopyId = BookCopyId.create();
    var listener = new LendingEventListener(informService);

    listener.on(new BookCopyReturned(readerId, bookCopyId, true));

    var commandCaptor = ArgumentCaptor.forClass(InformCommand.class);
    verify(informService).inform(commandCaptor.capture());
    assertThat(commandCaptor.getValue().readerId()).isEqualTo(readerId);
    assertThat(commandCaptor.getValue().type())
        .isEqualTo(NotificationType.BOOK_COPY_RETURNED_OVERDUE);
    assertThat(commandCaptor.getValue().message()).contains("overdue");
  }
}
