package com.example.library.notifications.application.command;

import com.example.library.notifications.domain.notification.NotificationType;
import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDateTime;

public record InformCommand(
    ReaderId readerId, NotificationType type, String message, LocalDateTime occurredOn) {}
