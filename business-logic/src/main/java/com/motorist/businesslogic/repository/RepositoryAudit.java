package com.motorist.businesslogic.repository;

import com.motorist.businesslogic.domain.EntityAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryAudit extends JpaRepository<EntityAudit, Long> {
}
