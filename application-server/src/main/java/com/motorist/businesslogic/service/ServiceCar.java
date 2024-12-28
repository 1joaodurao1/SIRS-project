package com.motorist.businesslogic.service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.motorist.businesslogic.domain.EntityCarAudit;
import com.motorist.businesslogic.domain.EntityCarConfiguration;
import com.motorist.businesslogic.repository.RepositoryCarAudit;
import com.motorist.businesslogic.repository.RepositoryCarConfiguration;
import com.motorist.businesslogic.service.errors.CarConfigurationNotFoundException;
import com.motorist.businesslogic.service.errors.FirmwareNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

@Service
public class ServiceCar {


    private final RepositoryCarConfiguration repositoryCarConfiguration;

    private final RepositoryCarAudit repositoryCarAudit;

    private static final String FIRMWARE_LOCATION = "firmware.txt";

    @Autowired
    public ServiceCar(
        final RepositoryCarConfiguration repositoryCarConfiguration,
        final RepositoryCarAudit repositoryCarAudit)
    {
        this.repositoryCarConfiguration = repositoryCarConfiguration;
        this.repositoryCarAudit = repositoryCarAudit;
    }

    public String getConfiguration() throws CarConfigurationNotFoundException {
       final List<EntityCarConfiguration> result = repositoryCarConfiguration.findAll();
       if (result.size() != 1) { // Check this later
           throw new CarConfigurationNotFoundException();
       }
       return result.get(0).getCarConfiguration();
    }

    public String modifyConfiguration(
        final String carConfiguration) throws CarConfigurationNotFoundException
    {
        //Depois ajustar com base no Json que recebemos
        final List<EntityCarConfiguration> result = repositoryCarConfiguration.findAll();
        if (result.size() != 1) { // Check this later
            throw new CarConfigurationNotFoundException();
        }

        final JsonObject currentCarConfiguration = JsonParser
            .parseString(result.get(0).getCarConfiguration())
            .getAsJsonObject();

        final JsonObject changes = JsonParser.parseString(carConfiguration).getAsJsonObject()
            .getAsJsonObject("content")
            .getAsJsonObject("changes");

        for (Map.Entry<String, JsonElement> entry : changes.entrySet()) {
            String key = entry.getKey();
            JsonElement newValue = entry.getValue();

            // Update the field if it exists in the currentCarConfiguration
            if (currentCarConfiguration.getAsJsonObject("configuration").has(key)) {
                currentCarConfiguration.getAsJsonObject("configuration").add(key, newValue);
            }
        }

        final String updatedConfiguration = currentCarConfiguration.toString();

        result.get(0).setCarConfiguration(updatedConfiguration);
        repositoryCarConfiguration.save(result.get(0));
        repositoryCarAudit.save(new EntityCarAudit("A user just modified the car configuration with the following: " + carConfiguration));
        return "Car configuration successfully modified:\n" + updatedConfiguration;
    }

    public String modifyFirmware(
        final String body) throws FirmwareNotFoundException
    {
        try{
            System.out.println("Here is the body: " + body);
            Path file = Path.of(FIRMWARE_LOCATION);
            Files.writeString(file, body);
        } catch (Exception e) {
            throw new FirmwareNotFoundException();
        }
        repositoryCarAudit.save(new EntityCarAudit("A user just modified the car firmware with the following: " + body));
        return "Firmware was successfully updated!";
    }

    public List<String> getLogs() {
        return repositoryCarAudit
            .findAll()
            .stream()
            .map(EntityCarAudit::getActionLog)
            .toList();
    }
}
