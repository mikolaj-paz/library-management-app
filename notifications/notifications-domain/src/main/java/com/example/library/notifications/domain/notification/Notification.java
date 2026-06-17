package com.example.library.notifications.domain.notification;

import com.example.library.sharedkernel.identifier.ReaderId;
import java.time.LocalDateTime;

public record Notification(
    ReaderId readerId, NotificationType type, String message, LocalDateTime occurredOn) {}
