package com.motorist.businesslogic.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class EntityCarConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "car_configuration" )
    private String carConfiguration;
    @Column(name = "maintenance_mode" )
    private boolean maintenanceMode;

    public EntityCarConfiguration() {}

    public EntityCarConfiguration (
            final String carConfiguration, final boolean maintenanceMode)
    {
        this.carConfiguration = carConfiguration;
        this.maintenanceMode = maintenanceMode;
    }

    public Long getId() {
        return id;
    }

    public String getCarConfiguration() {
        return carConfiguration;
    }
    public boolean getMaintenanceMode() {
        return maintenanceMode;
    }
    public void setMaintenanceMode(boolean maintenanceMode) {
        this.maintenanceMode = maintenanceMode;
    }

    public void setCarConfiguration(String carConfiguration) {
        this.carConfiguration = carConfiguration;
    }
}