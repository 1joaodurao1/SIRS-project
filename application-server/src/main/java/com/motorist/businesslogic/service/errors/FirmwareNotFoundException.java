package com.motorist.businesslogic.service.errors;

public class FirmwareNotFoundException extends Exception {

    public final static String CAR_FIRMWARE_NOT_FOUND = "Car firmware not found";
    public FirmwareNotFoundException() {
        super(CAR_FIRMWARE_NOT_FOUND);
    }

    public String getMessage () {
        return CAR_FIRMWARE_NOT_FOUND;
    }
}
