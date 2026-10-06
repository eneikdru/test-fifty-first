package com.eneik.generated.notification;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;

public class Booking {
    private final String bookingId;
    private final String masterId;
    private final String masterName;
    private final String customerPhone;
    private final String serviceName;
    private final double priceGel;
    private final Instant scheduledAt;
    private final AtomicReference<BookingStatus> status;

    public Booking(String bookingId, String masterId, String masterName, String customerPhone, String serviceName, double priceGel, Instant scheduledAt, BookingStatus status) {
        this.bookingId = bookingId;
        this.masterId = masterId;
        this.masterName = masterName;
        this.customerPhone = customerPhone;
        this.serviceName = serviceName;
        this.priceGel = priceGel;
        this.scheduledAt = scheduledAt;
        this.status = new AtomicReference<>(status != null ? status : BookingStatus.PENDING);
    }

    public String getBookingId() { return bookingId; }
    public String getMasterId() { return masterId; }
    public String getMasterName() { return masterName; }
    public String getCustomerPhone() { return customerPhone; }
    public String getServiceName() { return serviceName; }
    public double getPriceGel() { return priceGel; }
    public Instant getScheduledAt() { return scheduledAt; }
    public BookingStatus getStatus() { return status.get(); }

    public boolean compareAndSetStatus(BookingStatus expected, BookingStatus update) {
        return status.compareAndSet(expected, update);
    }
}
