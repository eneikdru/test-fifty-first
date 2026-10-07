package com.eneik.generated.repository;

import com.eneik.generated.model.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, String> {

    List<BookingEntity> findBySlotId(Long slotId);

    List<BookingEntity> findByMasterId(Long masterId);

    @Modifying
    @Query("UPDATE BookingEntity b SET b.status = :newStatus, b.updatedAt = :now WHERE b.id = :id AND b.status = :expectedStatus")
    int updateStatusAtomically(@Param("id") String id,
                               @Param("expectedStatus") String expectedStatus,
                               @Param("newStatus") String newStatus,
                               @Param("now") OffsetDateTime now);
}
