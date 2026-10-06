package com.eneik.generated.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterProfileRepository extends JpaRepository<MasterProfile, String> {
    List<MasterProfile> findByCity(String city);
}
