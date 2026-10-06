package com.eneik.generated.profile.repository;

import com.eneik.generated.profile.model.MasterService;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MasterServiceRepository extends JpaRepository<MasterService, Long> {
    List<MasterService> findByMasterProfileId(Long masterId);
}
