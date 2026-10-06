package com.eneik.generated.notification;

import org.springframework.stereotype.Component;

import java.time.Clock;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Component
public class TestSmsSender implements SmsSender {
    private final Clock clock;
    private final List<SmsMessage> sentMessages = Collections.synchronizedList(new ArrayList<>());

    public TestSmsSender() {
        this(Clock.systemUTC());
    }

    public TestSmsSender(Clock clock) {
        this.clock = clock;
    }

    @Override
    public void sendSms(String recipientPhone, String messageContent) {
        sentMessages.add(new SmsMessage(recipientPhone, messageContent, clock.instant()));
    }

    public List<SmsMessage> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public void clear() {
        sentMessages.clear();
    }
}
