package com.eneik.generated.messaging.gateway;

import com.eneik.generated.messaging.domain.Notification;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class InMemoryNotificationGateway implements NotificationGateway {
    private final List<Notification> dispatchedNotifications = Collections.synchronizedList(new ArrayList<>());

    @Override
    public boolean send(Notification notification) {
        dispatchedNotifications.add(notification);
        return true;
    }

    public List<Notification> getDispatchedNotifications() {
        return Collections.unmodifiableList(new ArrayList<>(dispatchedNotifications));
    }
}
