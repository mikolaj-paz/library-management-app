package com.example.library.notifications.infrastructure.out;

import com.example.library.notifications.application.port.out.NotificationSender;
import com.example.library.notifications.domain.notification.Notification;

public class ConsoleNotificationSender implements NotificationSender {

  @Override
  public void send(Notification notification) {
    System.out.printf(
        "[NOTIFICATION] type=%-25s readerId=%s message=%s%n",
        notification.type(), notification.readerId(), notification.message());
  }
}
