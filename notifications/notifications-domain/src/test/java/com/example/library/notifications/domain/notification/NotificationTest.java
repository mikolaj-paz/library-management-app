package com.example.library.notifications.domain.notification;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDateTime;
import org.junit.jupiter.api.Test;

class NotificationTest {

  @Test
  void should_create_notification_for_reader() {
    var readerId = ReaderId.create();
    var occurredOn = LocalDateTime.of(2026, 1, 1, 12, 0);

    var notification =
        new Notification(
            readerId, NotificationType.BOOK_COPY_LOANED, "Book copy was loaned", occurredOn);

    assertThat(notification.readerId()).isEqualTo(readerId);
    assertThat(notification.type()).isEqualTo(NotificationType.BOOK_COPY_LOANED);
    assertThat(notification.message()).isEqualTo("Book copy was loaned");
    assertThat(notification.occurredOn()).isEqualTo(occurredOn);
  }

  @Test
  void should_define_notification_types_for_lending_events() {
    assertThat(NotificationType.values())
        .containsExactly(
            NotificationType.BOOK_COPY_RESERVED,
            NotificationType.BOOK_COPY_LOANED,
            NotificationType.BOOK_COPY_RETURNED,
            NotificationType.BOOK_COPY_RETURNED_OVERDUE);
  }
}
