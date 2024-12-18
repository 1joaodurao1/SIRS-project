package com.motorist.businesslogic.repository;

import com.motorist.businesslogic.domain.EntityCarConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RepositoryCarConfiguration extends JpaRepository<EntityCarConfiguration, Long> {
}
