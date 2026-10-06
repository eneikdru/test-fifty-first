package com.eneik.generated.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {

    Optional<Booking> findByBookingReference(String bookingReference);

    List<Booking> findByMasterId(String masterId);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query("UPDATE Booking b SET b.status = :newStatus WHERE b.id = :id AND b.status = :currentStatus")
    int updateBookingStatusAtomically(@Param("id") Long id, @Param("currentStatus") String currentStatus, @Param("newStatus") String newStatus);
}
