package com.eneik.generated.messaging.domain;

import java.time.Instant;

public class Booking {
    private final String id;
    private final String customerId;
    private final String customerPhone;
    private final String customerEmail;
    private final String masterId;
    private final String serviceName;
    private final Instant appointmentTime;
    private BookingStatus status;
    private final Instant createdAt;

    public Booking(String id, String customerId, String customerPhone, String customerEmail,
                   String masterId, String serviceName, Instant appointmentTime,
                   BookingStatus status, Instant createdAt) {
        this.id = id;
        this.customerId = customerId;
        this.customerPhone = customerPhone;
        this.customerEmail = customerEmail;
        this.masterId = masterId;
        this.serviceName = serviceName;
        this.appointmentTime = appointmentTime;
        this.status = status;
        this.createdAt = createdAt;
    }

    public String getId() { return id; }
    public String getCustomerId() { return customerId; }
    public String getCustomerPhone() { return customerPhone; }
    public String getCustomerEmail() { return customerEmail; }
    public String getMasterId() { return masterId; }
    public String getServiceName() { return serviceName; }
    public Instant getAppointmentTime() { return appointmentTime; }
    public BookingStatus getStatus() { return status; }
    public void setStatus(BookingStatus status) { this.status = status; }
    public Instant getCreatedAt() { return createdAt; }
}
