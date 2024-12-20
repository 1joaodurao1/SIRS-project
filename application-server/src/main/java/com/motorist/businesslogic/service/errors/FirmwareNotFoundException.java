package com.motorist.businesslogic.service.errors;

public class FirmwareNotFoundException extends Exception {

    public FirmwareNotFoundException(String message) {
        super(message);
    }
}
