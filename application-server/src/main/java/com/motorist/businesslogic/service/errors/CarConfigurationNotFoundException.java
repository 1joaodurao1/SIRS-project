package com.motorist.businesslogic.service.errors;

public class CarConfigurationNotFoundException extends Exception{

    public final static String CAR_CONFIGURATION_NOT_FOUND = "Car configuration not found";
    public CarConfigurationNotFoundException() {
        super(CAR_CONFIGURATION_NOT_FOUND);
    }

    public String getMessage () {
        return CAR_CONFIGURATION_NOT_FOUND;
    }
}
