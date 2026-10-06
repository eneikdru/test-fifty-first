package com.eneik.generated.app.repository;

import com.eneik.generated.app.domain.Service;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {
    List<Service> findByProfessionalId(Long professionalId);
}
