package com.motorist.businesslogic.service.database;

import com.motorist.businesslogic.domain.EntityCarConfiguration;
import com.motorist.businesslogic.repository.RepositoryCarConfiguration;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component
public class DatabaseInitializer {

    private final RepositoryCarConfiguration repositoryCarConfiguration;

    @Autowired
    public DatabaseInitializer(
        final RepositoryCarConfiguration repositoryCarConfiguration)
    {
        this.repositoryCarConfiguration = repositoryCarConfiguration;
    }

    @PostConstruct
    public void init() {
        try {
            InputStream inputStream = getClass().getResourceAsStream("/car/defaultConfiguration.json");
            if (inputStream == null) {
                throw new IllegalArgumentException("JSON file not found in resources!");
            }

            if (!repositoryCarConfiguration.findAll().isEmpty()) {
                System.out.println("Database already initialized, not inserting values");
                return;
            }

            String configuration = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            EntityCarConfiguration carConfiguration = new EntityCarConfiguration();
            carConfiguration.setCarConfiguration(configuration);
            repositoryCarConfiguration.save(carConfiguration);

            System.out.println("Database initialized with configuration!");
        } catch (IOException e) {
            throw new RuntimeException("Failed to read configuration.json", e);
        }
    }
}

