package com.motorist.securedocument.validation;

import java.util.List;

import com.motorist.securedocument.validation.funtions.CheckValidation;
import com.motorist.securedocument.validation.funtions.ProtectValidation;
import com.motorist.securedocument.validation.funtions.UnprotectValidation;

public class ValidationHandler {

    public static void validateCommand(List<String> userInput) throws Exception {
        switch (userInput.get(0)) {
            case "protect":
                ProtectValidation.validateProtect(userInput);
                break;
            case "check":
                CheckValidation.validateCheck(userInput);
                break;
            case "unprotect":
                UnprotectValidation.validateUnprotect(userInput);
                break;
            default:
                throw new Exception("Invalid command");
        }
    }
    
}
