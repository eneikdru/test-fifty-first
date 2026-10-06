package com.eneik.generated.repository;

import com.eneik.generated.model.MasterProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterProfileRepository extends JpaRepository<MasterProfile, Long> {
    List<MasterProfile> findByCity(String city);
}
