package com.example.library.notifications.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;

import com.example.library.notifications.application.command.InformCommand;
import com.example.library.notifications.application.port.out.NotificationSender;
import com.example.library.notifications.domain.notification.Notification;
import com.example.library.notifications.domain.notification.NotificationType;
import com.example.library.sharedkernel.identifier.ReaderId;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InformServiceTest {

  @Mock private NotificationSender notificationSender;

  @Test
  void should_send_notification_from_inform_command() {
    var readerId = ReaderId.create();
    var command =
        new InformCommand(readerId, NotificationType.BOOK_COPY_RETURNED, "Thanks for returning");
    var service = new InformService(notificationSender);

    service.inform(command);

    var notificationCaptor = ArgumentCaptor.forClass(Notification.class);
    verify(notificationSender).send(notificationCaptor.capture());
    assertThat(notificationCaptor.getValue().readerId()).isEqualTo(readerId);
    assertThat(notificationCaptor.getValue().type()).isEqualTo(NotificationType.BOOK_COPY_RETURNED);
    assertThat(notificationCaptor.getValue().message()).isEqualTo("Thanks for returning");
  }
}
