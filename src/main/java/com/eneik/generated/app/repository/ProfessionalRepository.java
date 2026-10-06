package com.eneik.generated.app.repository;

import com.eneik.generated.app.domain.Professional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProfessionalRepository extends JpaRepository<Professional, Long> {
    Optional<Professional> findByFacebookId(String facebookId);
    Optional<Professional> findByPhoneNumber(String phoneNumber);
}
