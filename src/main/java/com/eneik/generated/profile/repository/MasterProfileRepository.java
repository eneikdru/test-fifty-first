package com.eneik.generated.profile.repository;

import com.eneik.generated.profile.model.MasterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MasterProfileRepository extends JpaRepository<MasterProfile, Long> {
    Optional<MasterProfile> findByFacebookId(String facebookId);
    Optional<MasterProfile> findByPhoneNumber(String phoneNumber);
}
