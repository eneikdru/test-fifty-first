package com.eneik.generated.messaging.gateway;

import com.eneik.generated.messaging.domain.Notification;

public interface NotificationGateway {
    boolean send(Notification notification);
}
