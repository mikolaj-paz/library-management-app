package com.example.library.notifications.infrastructure.config;

import com.example.library.notifications.application.port.in.IInform;
import com.example.library.notifications.application.port.out.NotificationSender;
import com.example.library.notifications.application.service.Informing;
import com.example.library.notifications.infrastructure.in.events.LendingEventListener;
import com.example.library.notifications.infrastructure.out.ConsoleNotificationSender;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class NotificationsConfig {

  @Bean
  NotificationSender notificationSender() {
    return new ConsoleNotificationSender();
  }

  @Bean
  IInform informing(NotificationSender notificationSender) {
    return new Informing(notificationSender);
  }

  @Bean
  LendingEventListener loanEventListener(IInform informing) {
    return new LendingEventListener(informing);
  }
}
