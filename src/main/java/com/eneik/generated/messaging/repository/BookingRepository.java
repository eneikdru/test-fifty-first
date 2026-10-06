package com.eneik.generated.messaging.repository;

import com.eneik.generated.messaging.domain.Booking;
import com.eneik.generated.messaging.domain.BookingStatus;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;

@Repository
public class BookingRepository {
    private final ConcurrentHashMap<String, Booking> storage = new ConcurrentHashMap<>();

    public Booking save(Booking booking) {
        storage.put(booking.getId(), booking);
        return booking;
    }

    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(storage.get(id));
    }

    public List<Booking> findAll() {
        return new ArrayList<>(storage.values());
    }

    public List<Booking> findUpcomingBookings(Instant startWindow, Instant endWindow) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : storage.values()) {
            if (booking.getStatus() == BookingStatus.CONFIRMED
                    && !booking.getAppointmentTime().isBefore(startWindow)
                    && !booking.getAppointmentTime().isAfter(endWindow)) {
                result.add(booking);
            }
        }
        return result;
    }

    /**
     * Atomically updates status from expectedStatus to newStatus.
     * Returns true if status was updated successfully, false otherwise.
     */
    public boolean updateStatusAtomically(String bookingId, BookingStatus expectedStatus, BookingStatus newStatus) {
        AtomicBoolean updated = new AtomicBoolean(false);
        storage.computeIfPresent(bookingId, (id, current) -> {
            if (current.getStatus() == expectedStatus) {
                current.setStatus(newStatus);
                updated.set(true);
            }
            return current;
        });
        return updated.get();
    }
}
