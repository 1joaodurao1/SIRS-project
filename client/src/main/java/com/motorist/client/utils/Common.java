package com.motorist.client.utils;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class Common {

    public static String checkPassword(){

        String path = System.getProperty("user.dir") + "/client/src/main/password.txt";
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

    public static String hash(String password) {
        // hash the password
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    
    
}
