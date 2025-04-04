package com.motorist.businesslogic.service.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.motorist.businesslogic.domain.EntityCarConfiguration;
import com.motorist.businesslogic.repository.RepositoryCarConfiguration;

import jakarta.annotation.PostConstruct;

@Component
public class DatabaseInitializer {

    private final RepositoryCarConfiguration repositoryCarConfiguration;

    @Autowired
    public DatabaseInitializer(
        final RepositoryCarConfiguration repositoryCarConfiguration)
    {
        this.repositoryCarConfiguration = repositoryCarConfiguration;
    }

    @PostConstruct
    public void init() {
     
            
        try {
            InputStream inputStream = getClass().getResourceAsStream("/DBinitializationvalues/defaultConfiguration.json");

            InputStream secret = getClass().getResourceAsStream("/DBinitializationvalues/serverSecret.key");
            byte[] secretKeyBytes = secret.readAllBytes();
            SecretKeySpec secretKey = new SecretKeySpec(secretKeyBytes, "AES");

            InputStream ivInputStream = getClass().getResourceAsStream("/DBinitializationvalues/iv.bytes");
            byte[] ivBytes = ivInputStream.readAllBytes();
            IvParameterSpec iv = new IvParameterSpec(ivBytes);

            if (inputStream == null) {
                throw new IllegalArgumentException("JSON file not found in resources!");
            }

            if (!repositoryCarConfiguration.findAll().isEmpty()) {
                System.out.println("Database already initialized, not inserting values");
                return;
            }

            String configuration = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

            try{
                // Encrypt the car configuration using AES-CBC
                String encryptedConfiguration = encryptCarConfiguration(configuration, secretKey, iv);
                for (int i = 0; i < 2; i++) {
                    EntityCarConfiguration carConfiguration = new EntityCarConfiguration(encryptedConfiguration,false);
                    repositoryCarConfiguration.save(carConfiguration);
                }
                System.out.println("Database initialized with configuration!");
            } catch (GeneralSecurityException e) {
                System.out.println(e.getMessage());
            }

        } catch (Exception e) {
            System.out.println("Failed to read a file");
        }
        
    }

    private String encryptCarConfiguration(String configuration, SecretKey secretKey, IvParameterSpec iv) throws GeneralSecurityException {
        // Set up AES Cipher in CBC mode
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        // Encrypt the configuration string
        byte[] encryptedBytes = cipher.doFinal(configuration.getBytes(StandardCharsets.UTF_8));

        // Return the encrypted string as a Base64 encoded string
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }

    private IvParameterSpec loadIv(String path) throws IOException {
        // Read the IV from the file
        File ivFile = new File(getClass().getResource(path).getFile());
        byte[] ivBytes = new byte[(int) ivFile.length()];
        try (FileInputStream fis = new FileInputStream(ivFile)) {
            fis.read(ivBytes);
        }
        return new IvParameterSpec(ivBytes);
    }

    private static byte[] readFileBytes(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] fileBytes = new byte[fis.available()];
            fis.read(fileBytes);
            return fileBytes;
        }
    }
}

