package com.example.library.notifications.application.service;

import com.example.library.notifications.application.command.InformCommand;
import com.example.library.notifications.application.port.in.IInform;
import com.example.library.notifications.application.port.out.NotificationSender;
import com.example.library.notifications.domain.notification.Notification;

public class Informing implements IInform {

  private final NotificationSender notificationSender;

  public Informing(NotificationSender notificationSender) {
    this.notificationSender = notificationSender;
  }

  @Override
  public void inform(InformCommand data) {

    var notification =
        new Notification(data.readerId(), data.type(), data.message(), data.occurredOn());

    notificationSender.send(notification);
  }
}
