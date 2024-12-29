package com.motorist.securedocument.validation.funtions;

import java.io.File;
import java.util.List;

public class CheckValidation {

    public static void validateCheck(List<String> userInput) throws Exception {
        
        if (userInput.size() != 4) {
            throw new Exception("Invalid check command");
        }

        if ( !userInput.get(1).endsWith(".json")) {
            throw new Exception("Invalid input file");
        }

        if ( !userInput.get(3).equals("user") && 
        !userInput.get(3).equals("owner") && !userInput.get(3).equals("mechanic") ) {
            throw new Exception("Invalid sender");
        }

        if ( !userInput.get(2).equals("user") && !userInput.get(2).equals("owner") 
        && !userInput.get(2).equals("mechanic") ) {
            throw new Exception("Invalid receiver");
        }

        // open input file to see if it exists
        File inputFile = new File(userInput.get(1));
        if (!inputFile.exists()) {
            throw new Exception("Input file does not exist");
        }


    }
    
}
