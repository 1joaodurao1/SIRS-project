package com.motorist.businesslogic.domain;

import jakarta.persistence.*;

@Entity
public class EntityCarConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "car_configuration")
    private String carConfiguration;

    public EntityCarConfiguration() {}

    public EntityCarConfiguration (
            final String carConfiguration)
    {
        this.carConfiguration = carConfiguration;
    }

    public Long getId() {
        return id;
    }

    public String getCarConfiguration() {
        return carConfiguration;
    }

    public void setCarConfiguration(String carConfiguration) {
        this.carConfiguration = carConfiguration;
    }
}