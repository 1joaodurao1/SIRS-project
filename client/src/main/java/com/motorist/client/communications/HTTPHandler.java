package com.motorist.client.communications;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.motorist.client.utils.Common;

public class HTTPHandler {

    private final String base_car ; // URL of the server

    private final String base_manufaturer ; // URL of the server

    private final RestTemplate restTemplate = new RestTemplate();


    public HTTPHandler(String base_car , String base_manufaturer) {
        this.base_car = "https://" + base_car + "/api/car";
        this.base_manufaturer = "https://" + base_manufaturer + "/api/manufacturer";

    }

    public void setRestTemplate( String role) {
        System.setProperty("javax.net.ssl.keyStore", "client/src/main/resources/tls/" + role + ".p12");
        System.setProperty("javax.net.ssl.keyStorePassword", "changeme");
        System.setProperty("javax.net.ssl.trustStore", "client/src/main/resources/tls/" + role + "truststore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "changeme");
    }

    public JsonObject sendPayload(JsonObject payload , String command) throws Exception {

        String payloadToString = payload.toString();
        String response ;
        response = defaultSend(payloadToString, command);
       
        return JsonParser.parseString(response).getAsJsonObject();

    }

    private String defaultSend (String payload , String command) throws Exception {

        System.out.println("Sending payload: " + payload);
        return restTemplate.postForObject(base_car + "/" + command, payload, String.class);
    }

    public JsonObject sendGetRequest(String ds , String command , String role, String base) throws Exception {

        String base_url = base.equals("car") ? base_car : base_manufaturer;
        // Create HttpHeaders
        HttpHeaders headers = new HttpHeaders();
        headers.set("Digital-Signature", ds); // Your digital signature
        // Create an HttpEntity with the headers
        if (role.equals("owner") && base.equals("car")) {
            String hash_password = Common.checkPassword();
            headers.set("Password", hash_password);
        }
        // Create an HttpEntity with headers (no body needed for GET)
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Use exchange instead of getForObject to include headers
        ResponseEntity<String> response = restTemplate.exchange(
            base_url + "/" + command, // URL
            HttpMethod.GET,           // HTTP method
            entity,                   // HttpEntity containing headers
            String.class              // Response type
        );
        System.out.println(response.getBody());
        return JsonParser.parseString(response.getBody()).getAsJsonObject();
    }
    //TODO : handle errors de HTTPS
}
