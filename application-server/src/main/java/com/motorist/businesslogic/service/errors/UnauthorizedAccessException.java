package com.motorist.businesslogic.service.errors;

public class UnauthorizedAccessException extends Exception{

    public final static String UNAUTHORIZED_ACCESS = "You are not authorized to access this.";
    public UnauthorizedAccessException() {
        super(UNAUTHORIZED_ACCESS);
    }

    public String getMessage () {
            return UNAUTHORIZED_ACCESS;
    }
}
