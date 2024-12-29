package com.motorist.client.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Common {

    public String checkPassword(){

        String path = System.getProperty("user.dir") + "/client/src/main/passwords.txt";
            Path filePath = Paths.get(path);

            if ( Files.exists(filePath) ) {
                try {
                    return Files.readString(filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        return null;
    }
    
}
