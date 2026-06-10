package com.example.library.notifications.application.port.out;

import com.example.library.notifications.domain.notification.Notification;

public interface NotificationSender {

  void send(Notification notification);
}
