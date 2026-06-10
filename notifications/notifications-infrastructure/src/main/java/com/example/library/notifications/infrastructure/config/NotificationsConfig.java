package com.example.library.notifications.infrastructure.config;

import com.example.library.notifications.application.port.in.IInform;
import com.example.library.notifications.application.port.out.NotificationSender;
import com.example.library.notifications.application.service.InformService;
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
  IInform inform(NotificationSender notificationSender) {
    return new InformService(notificationSender);
  }

  @Bean
  LendingEventListener loanEventListener(IInform inform) {
    return new LendingEventListener(inform);
  }
}
