package com.motorist.securedocument.validation.funtions;

import java.io.File;
import java.util.List;

public class ProtectValidation {

    public static void validateProtect(List<String> userInput) throws Exception {

        if (userInput.size() != 5) {
            throw new Exception("Invalid protect command");
        }

        if ( !userInput.get(3).equals("user") && 
        !userInput.get(3).equals("owner") && !userInput.get(3).equals("mechanic") ) {
            throw new Exception("Invalid receiver");
        }

        // see if the input file is a Json file and exists
        if (!userInput.get(1).endsWith(".json")) {
            throw new Exception("Invalid input file");
        }

        // open input file to see if it exists
        File inputFile = new File(userInput.get(1));
        if (!inputFile.exists()) {
            throw new Exception("Input file does not exist");
        }

    }
    
}
