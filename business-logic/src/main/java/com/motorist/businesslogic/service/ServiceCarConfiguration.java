package com.motorist.businesslogic.service;

import com.motorist.businesslogic.domain.EntityCarConfiguration;
import com.motorist.businesslogic.repository.RepositoryAudit;
import com.motorist.businesslogic.repository.RepositoryCarConfiguration;
import com.motorist.businesslogic.service.errors.CarConfigurationNotFound;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCarConfiguration {

    @Autowired
    private RepositoryCarConfiguration repositoryCarConfiguration;

    @Autowired
    private RepositoryAudit repositoryAudit;

    public Either<CarConfigurationNotFound, String> getCarConfiguration() {
       List<EntityCarConfiguration> result = repositoryCarConfiguration.findAll();
       if (result.size() != 1) {
           return Either.left(new CarConfigurationNotFound("Configuration not found"));
       }
       return Either.right(result.get(0).getCarConfiguration());
    }
}
