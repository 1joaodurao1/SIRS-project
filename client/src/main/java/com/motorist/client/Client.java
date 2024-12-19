package com.motorist.client;

import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;

public class Client {
    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        System.setProperty("javax.net.ssl.keyStore", "client/src/main/resources/tls/user.p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeme");
        System.setProperty("javax.net.ssl.trustStore", "client/src/main/resources/tls/usertruststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeme");

        String serverUrl = "https://localhost:8443/api/car"; // URL of the server

        // Create RestTemplate instance for making HTTP requests
        RestTemplate restTemplate = new RestTemplate();

        // Example of sending a GET request
        String response = restTemplate.getForObject(serverUrl + "/test", String.class);

        // Print the retrieved cars
        System.out.println(response);
    }
}