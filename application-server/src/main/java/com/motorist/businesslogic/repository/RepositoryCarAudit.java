package com.motorist.businesslogic.repository;

import com.motorist.businesslogic.domain.EntityCarAudit;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryCarAudit extends JpaRepository<EntityCarAudit, Long> {
}
