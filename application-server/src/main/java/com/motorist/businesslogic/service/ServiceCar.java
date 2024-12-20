package com.motorist.businesslogic.service;

import com.motorist.businesslogic.domain.EntityCarAudit;
import com.motorist.businesslogic.domain.EntityCarConfiguration;
import com.motorist.businesslogic.repository.RepositoryCarAudit;
import com.motorist.businesslogic.repository.RepositoryCarConfiguration;
import com.motorist.businesslogic.service.errors.CarConfigurationNotFoundException;
import io.vavr.control.Either;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ServiceCar {


    private final RepositoryCarConfiguration repositoryCarConfiguration;

    private final RepositoryCarAudit repositoryCarAudit;

    @Autowired
    public ServiceCar(
        final RepositoryCarConfiguration repositoryCarConfiguration,
        final RepositoryCarAudit repositoryCarAudit)
    {
        this.repositoryCarConfiguration = repositoryCarConfiguration;
        this.repositoryCarAudit = repositoryCarAudit;
    }

    public Either<CarConfigurationNotFoundException, String> getConfiguration() {
       List<EntityCarConfiguration> result = repositoryCarConfiguration.findAll();
       if (result.size() != 1) { // Check this later
           return Either.left(new CarConfigurationNotFoundException("Configuration not found"));
       }
       return Either.right(result.get(0).getCarConfiguration());
    }

    public Either<CarConfigurationNotFoundException, String> modifyConfiguration(
        final String carConfiguration)
    {
        List<EntityCarConfiguration> result = repositoryCarConfiguration.findAll();
        if (result.size() != 1) { // Check this later
            return Either.left(new CarConfigurationNotFoundException("Configuration not found"));
        }
        result.get(0).setCarConfiguration(carConfiguration);
        repositoryCarConfiguration.save(result.get(0));
        return Either.right("Car configuration successfully modified !");
    }

    public String modifyFirmware()
    {
        return "Firmware was updated !";
    }

    public List<String> getLogs() {
        return repositoryCarAudit
            .findAll()
            .stream()
            .map(EntityCarAudit::getActionLog)
            .toList();
    }
}
