package com.motorist.businesslogic.service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.spec.KeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.motorist.businesslogic.domain.EntityCarAudit;
import com.motorist.businesslogic.domain.EntityCarConfiguration;
import com.motorist.businesslogic.repository.RepositoryCarAudit;
import com.motorist.businesslogic.repository.RepositoryCarConfiguration;
import com.motorist.businesslogic.utils.JsonHandler;
import com.motorist.securedocument.core.CryptographicOperations;
import static com.motorist.securedocument.core.CryptographicOperations.addSecurity;
import static com.motorist.securedocument.core.CryptographicOperations.doCheck;
import static com.motorist.securedocument.core.CryptographicOperations.removeSecurity;
import com.motorist.securedocument.core.confidentiality.func.SymmetricCipherImpl;
import com.motorist.securedocument.core.integrity.func.DigitalSignatureImpl;

@Service
public class ServiceCar {


    private final RepositoryCarConfiguration repositoryCarConfiguration;

    private final RepositoryCarAudit repositoryCarAudit;

    private static final String FIRMWARE_LOCATION = "firmware.txt";

    private static final int MODULE_ID = 2;
    

    @Autowired
    public ServiceCar(
        final RepositoryCarConfiguration repositoryCarConfiguration,
        final RepositoryCarAudit repositoryCarAudit)
    {
        this.repositoryCarConfiguration = repositoryCarConfiguration;
        this.repositoryCarAudit = repositoryCarAudit;
    }

    public String getConfiguration(
        final String digitalSignature,
        final Optional<String> password) throws Exception
    {
        
        JsonObject response = JsonHandler.createBaseJson();
        ArrayList<String> accessControlList = new ArrayList<>(Arrays.asList( "owner", "mechanic"));
        String sender = hasAccess("read", digitalSignature , null);

        if ( ! accessControlList.contains(sender) || sender.isBlank()) {
            response = JsonHandler.addErrorMessage(response, "You are not authorized to access this.");
            return addSecurity(response, "server", sender , MODULE_ID).toString();
        }

        System.out.println("Authorized owner");
        final List<EntityCarConfiguration> result = repositoryCarConfiguration.findAll();
        if (result.size() != 2) { // Check this later
            System.out.println("Car configurations are not 2");
            response = JsonHandler.addErrorMessage(response, "Car configuration not found");
            return addSecurity(response, "server", sender , MODULE_ID).toString();
        }

        result.sort(Comparator.comparing(EntityCarConfiguration::getId));

        System.err.println("\n\nSender: " + sender+ "\n\n");

        byte[] symmetric_key = readFileBytes("/DBinitializationvalues/serverSecret.key");
        byte[] iv = readFileBytes("/DBinitializationvalues/iv.bytes");

        // handle maintenance mode
        JsonObject carConfiguration = null;
        if ( result.get(0).getMaintenanceMode() == true) {
            if ( !password.isEmpty() ) {
                String hash_password = password.get();
                SecretKeySpec key = deriveKeyFromPassword(hash_password, symmetric_key);
                carConfiguration = SymmetricCipherImpl.decryptDB(result.get(0).getCarConfiguration(),key.getEncoded(),iv);
            }else{
                if ( sender.equals("owner")){
                    response = JsonHandler.addErrorMessage(response, "Maintenance mode is on. You do not have access.");
                    return addSecurity(response, "server", sender , MODULE_ID).toString();
                }
                else{
                    carConfiguration = SymmetricCipherImpl.decryptDB(result.get(1).getCarConfiguration(),symmetric_key,iv);
                }
                
            }
            
        }else{
            if ( sender.equals("mechanic")){
                response = JsonHandler.addErrorMessage(response, "You are not authorized to access this.");
                return addSecurity(response, "server", sender , MODULE_ID).toString();
            }
            carConfiguration = SymmetricCipherImpl.decryptDB(result.get(0).getCarConfiguration(),symmetric_key,iv);
        }

        response = JsonHandler.addConfiguration(response, carConfiguration);
        return addSecurity(response, "server", sender , MODULE_ID).toString();

    }

    public String modifyConfiguration(
        final String carConfiguration) throws Exception
    {

        JsonObject requestBody = JsonParser.parseString(carConfiguration).getAsJsonObject();

        JsonObject response = JsonHandler.createBaseJson();

        ArrayList<String> accessControlList = new ArrayList<>(Arrays.asList( "owner", "mechanic"));
        String sender = hasAccess("change",null, requestBody);
        if ( ! accessControlList.contains(sender) || sender.isBlank()) {
            response = JsonHandler.addErrorMessage(response, "You are not authorized to access this.");
            return addSecurity(response, "server", sender , MODULE_ID).toString();
        }

        JsonObject decryptedJson = removeSecurity(requestBody, sender , MODULE_ID);
        JsonObject content  = decryptedJson.getAsJsonObject("content");
        JsonObject metadata = decryptedJson.getAsJsonObject("metadata");
        String ds = metadata.get("signature").getAsString();
        final JsonObject changes = content.getAsJsonObject("configurations");

        //Depois ajustar com base no Json que recebemos
        final List<EntityCarConfiguration> result = repositoryCarConfiguration.findAll();
        if (result.size() != 2) { // Check this later
            response = JsonHandler.addErrorMessage(response, "Car configuration not found");
            return addSecurity(response, "server", sender , MODULE_ID).toString();
        }
        result.sort(Comparator.comparing(EntityCarConfiguration::getId));
        


        byte[] symmetric_key = readFileBytes("/DBinitializationvalues/serverSecret.key");
        byte[] iv = readFileBytes("/DBinitializationvalues/iv.bytes");
        JsonObject currentCarConfiguration = null;
        if ( result.get(0).getMaintenanceMode() == true){
            if ( sender.equals("owner")){
                String hash_password = content.get("password").getAsString();
                SecretKeySpec key = deriveKeyFromPassword(hash_password, symmetric_key);
                currentCarConfiguration = SymmetricCipherImpl.decryptDB(result.get(0).getCarConfiguration(),key.getEncoded(),iv);
                JsonObject updatedCarConfiguration = applyChanges(changes, currentCarConfiguration);
                final String updatedConfiguration = SymmetricCipherImpl.encryptDB(updatedCarConfiguration, key.getEncoded(), iv);
                result.get(0).setCarConfiguration(updatedConfiguration);
                repositoryCarConfiguration.save(result.get(0));
            } else {
                
                // maintenance mode is on, and not owner , so chnages are applied to the default configuration
                currentCarConfiguration = SymmetricCipherImpl.decryptDB(result.get(1).getCarConfiguration(),symmetric_key,iv);
                JsonObject updatedCarConfiguration = applyChanges(changes, currentCarConfiguration);
                System.out.println("Updated configuration: " + updatedCarConfiguration);
                final String updatedConfiguration = SymmetricCipherImpl.encryptDB(updatedCarConfiguration, symmetric_key, iv);
                System.out.println("Updated configuration: " + updatedConfiguration);
                result.get(1).setCarConfiguration(updatedConfiguration);
                repositoryCarConfiguration.save(result.get(1));
            }
            
        } else {
            if ( sender.equals("mechanic")){
                response = JsonHandler.addErrorMessage(response, "You are not authorized to access this.");
                return addSecurity(response, "server", sender , MODULE_ID).toString();
            }
            currentCarConfiguration = SymmetricCipherImpl.decryptDB(result.get(0).getCarConfiguration(),symmetric_key,iv);
            JsonObject updatedCarConfiguration = applyChanges(changes, currentCarConfiguration);
            final String updatedConfiguration = SymmetricCipherImpl.encryptDB(updatedCarConfiguration, symmetric_key, iv);
            result.get(0).setCarConfiguration(updatedConfiguration);
            repositoryCarConfiguration.save(result.get(0));
        }

        repositoryCarAudit.save(new EntityCarAudit("change", sender ,ds,changes.toString()));

        return addSecurity(JsonHandler.standartResponse(response), "server", sender, MODULE_ID).toString();
    }

    public String modifyFirmware(
        final String body) throws Exception
    {
        JsonObject response = JsonHandler.createBaseJson();
        JsonObject requestBody = JsonParser.parseString(body).getAsJsonObject();
        ArrayList<String> accessControlList = new ArrayList<>(Arrays.asList( "mechanic"));
        String sender = hasAccess("update",null, requestBody);
        if ( ! accessControlList.contains(sender) || sender.isBlank()) {
            response = JsonHandler.addErrorMessage(response, "You are not authorized to access this.");
            return addSecurity(response, "server", sender , MODULE_ID).toString();
        }

        JsonObject decryptedJson = removeSecurity(requestBody, sender , MODULE_ID);
        JsonObject metadata = decryptedJson.getAsJsonObject("metadata");
        String ds = metadata.get("signature").getAsString();
        JsonObject content  = decryptedJson.getAsJsonObject("content");
        String firmware = CryptographicOperations.unprotectFirmware(content);
        if ( firmware.isBlank()) {
            response = JsonHandler.addErrorMessage(response, "Firmware not authorized by manufacturer.");
            return addSecurity(response, "server", sender , MODULE_ID).toString();
        }
        Path file = Path.of(FIRMWARE_LOCATION);
        Files.writeString(file, firmware);
        
        repositoryCarAudit.save(new EntityCarAudit("update", sender ,ds, firmware));
        System.out.println("Firmware updated successfully");
        return addSecurity(JsonHandler.standartResponse(response), "server", sender, MODULE_ID).toString();
    }

    public String getLogs(final String digitalSignature) throws Exception {

        JsonObject response = JsonHandler.createBaseJson();
        ArrayList<String> accessControlList = new ArrayList<>(Arrays.asList( "owner"));
        String sender = hasAccess("view", digitalSignature , null);

        if ( ! accessControlList.contains(sender) || sender.isBlank()) {
            response = JsonHandler.addErrorMessage(response, "You are not authorized to access this.");
            return addSecurity(response, "server", sender , MODULE_ID).toString();
        }
        
         // Check if the table has at least one row
        long count = repositoryCarAudit.count();
        if (count == 0) {
            response = JsonHandler.addErrorMessage(response, "No logs found.");
            return addSecurity(response, "server", sender, MODULE_ID).toString();
        }

        List<JsonObject> result = repositoryCarAudit
            .findAll().stream()
            .map(audit -> {
                JsonObject json = new JsonObject();
                json.addProperty("actionLog", audit.getActionLog());
                json.addProperty("typeUser", audit.getUser());
                json.addProperty("digitalSignature", audit.getDS());
                json.addProperty("configuration", audit.getConfiguration());
                return json;
            })
            .collect(Collectors.toList());
        response = JsonHandler.addLogs(response, result);
        return addSecurity(response, "server", sender , MODULE_ID).toString();
    }

    public String setMaintenance(final String body) throws Exception {

        JsonObject response = JsonHandler.createBaseJson();
        JsonObject requestBody = JsonParser.parseString(body).getAsJsonObject();
        ArrayList<String> accessControlList = new ArrayList<>(Arrays.asList("owner"));
        String sender = hasAccess("maintenance", null, requestBody);
        if (!accessControlList.contains(sender) || sender.isBlank()) {
            response = JsonHandler.addErrorMessage(response, "You are not authorized to access this.");
            return addSecurity(response, "server", sender, MODULE_ID).toString();
        }
        JsonObject decryptedJson = removeSecurity(requestBody, sender, MODULE_ID);
        JsonObject metadata = decryptedJson.getAsJsonObject("metadata");
        String ds = metadata.get("signature").getAsString();
        JsonObject content = decryptedJson.getAsJsonObject("content");

        String maintenanceDetails = content.get("maintenance_mode").getAsString();
        String password = content.get("password").getAsString();

        final List<EntityCarConfiguration> result = repositoryCarConfiguration.findAll();
        if (result.size() != 2) { // Check this later
            response = JsonHandler.addErrorMessage(response, "Car configuration not found");
            return addSecurity(response, "server", sender , MODULE_ID).toString();
        }
        result.sort(Comparator.comparing(EntityCarConfiguration::getId));


        
        byte[] symmetric_key = readFileBytes("/DBinitializationvalues/serverSecret.key");
        byte[] iv = readFileBytes("/DBinitializationvalues/iv.bytes");
        

        // If it is to set the maintenance mode on
        if (maintenanceDetails.equals("true")) {
            System.err.println("Setting maintenance mode on");
            if ( result.get(0).getMaintenanceMode() == true){
                response = JsonHandler.addErrorMessage(response, "Maintenance mode is already on.");
                return addSecurity(response, "server", sender, MODULE_ID).toString();
            }
            JsonObject currentCarConfiguration = SymmetricCipherImpl.decryptDB(result.get(0).getCarConfiguration(),symmetric_key,iv);
            result.get(0).setMaintenanceMode(true);
            SecretKeySpec key = deriveKeyFromPassword(password, symmetric_key);
            String updatedCarConfiguration = SymmetricCipherImpl.encryptDB(currentCarConfiguration, key.getEncoded(), iv);
            result.get(0).setCarConfiguration(updatedCarConfiguration);
            repositoryCarConfiguration.save(result.get(0));
            
        } else{
            System.err.println("Setting maintenance mode off");
            if ( result.get(0).getMaintenanceMode() == false){
                response = JsonHandler.addErrorMessage(response, "Maintenance mode is already off.");
                return addSecurity(response, "server", sender, MODULE_ID).toString();
            }
            SecretKeySpec key = deriveKeyFromPassword(password, symmetric_key);
            JsonObject currentCarConfiguration = SymmetricCipherImpl.decryptDB(result.get(0).getCarConfiguration(),key.getEncoded(),iv);
            result.get(0).setMaintenanceMode(false);
            String updatedCarConfiguration = SymmetricCipherImpl.encryptDB(currentCarConfiguration, symmetric_key, iv);
            result.get(0).setCarConfiguration(updatedCarConfiguration);
            repositoryCarConfiguration.save(result.get(0));
            // Resetting the configuration to the default one
            InputStream inputStream = getClass().getResourceAsStream("/DBinitializationvalues/defaultConfiguration.json");
            String defaultconfig = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
            JsonObject defaultConfigJson = JsonParser.parseString(defaultconfig).getAsJsonObject();
            result.get(1).setCarConfiguration(SymmetricCipherImpl.encryptDB(defaultConfigJson, symmetric_key, iv));
            repositoryCarConfiguration.save(result.get(1));
        }

        // Save maintenance details to the database or perform necessary operations
        // Assuming there is a method to handle this in the repository
        repositoryCarAudit.save(new EntityCarAudit("setMaintenance", sender, ds, maintenanceDetails));

        response = JsonHandler.standartResponse(response);
        return addSecurity(JsonHandler.standartResponse(response), "server", sender, MODULE_ID).toString();
    }

    // -----------------Private methods--------------------

    private static String hasAccess (  String command , String ds, JsonObject requestBody) throws Exception{
        ArrayList<String> possibleUsers = new ArrayList<>(Arrays.asList( "owner", "user", "mechanic"));
        for ( String user : possibleUsers){
            System.err.println("User: " + user);
            switch(command) {
                case "read":
                case "view":
                    if (DigitalSignatureImpl.checkGetRequest(command, user,ds , MODULE_ID)) {
                        return user;
                    }
                    break;
                default:
                    try {
                        if ( doCheck(requestBody, user , "server", MODULE_ID)){
                            return user;
                        }
                        break;
                    } catch (Exception e) {
                        System.out.println(e.getMessage());
                    }
                    
            }
        }
       return "";
    }


    private byte[] readFileBytes(String filename) throws IOException {
        System.out.println(filename);

        InputStream file = getClass().getResourceAsStream(filename);
        return file.readAllBytes();

    }

    public SecretKeySpec deriveKeyFromPassword(String password, byte[] existingKey) throws Exception {
        // Use the existing key as the salt
        byte[] salt = existingKey;

        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, 10000, 128);
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        return new SecretKeySpec(keyBytes, "AES");
    }

    private static JsonObject applyChanges(JsonObject changes, JsonObject currentCarConfiguration) {
        JsonObject configuration = currentCarConfiguration.getAsJsonObject("configuration");
    
        for (Map.Entry<String, JsonElement> entry : changes.entrySet()) {
            String key = entry.getKey();
            JsonElement newValue = entry.getValue();
    
            // Update the field if it exists in the currentCarConfiguration
            if (configuration.has(key)) {
                JsonElement currentElement = configuration.get(key);
    
                if (newValue.isJsonObject() && currentElement.isJsonObject()) {
                    JsonObject currentObject = currentElement.getAsJsonObject();
                    JsonObject newObject = newValue.getAsJsonObject();
                    for (Map.Entry<String, JsonElement> entry2 : newObject.entrySet()) {
                        currentObject.add(entry2.getKey(), entry2.getValue());
                    }
                } else if (newValue.isJsonArray() && currentElement.isJsonArray()) {
                    JsonArray currentArray = currentElement.getAsJsonArray();
                    JsonArray newArray = newValue.getAsJsonArray();
                    for (JsonElement newElement : newArray) {
                        JsonObject newObject = newElement.getAsJsonObject();
                        boolean found = false;
                        for (JsonElement currentElementInArray : currentArray) {
                            JsonObject currentObjectInArray = currentElementInArray.getAsJsonObject();
                            for (Map.Entry<String, JsonElement> entry2 : newObject.entrySet()) {
                                String key2 = entry2.getKey();
                                JsonElement newValue2 = entry2.getValue();
                                if (currentObjectInArray.has(key2)) {
                                    currentObjectInArray.add(key2, newValue2);
                                    found = true;
                                    break;
                                }
                            }
                            if (found) {
                                break;
                            }
                        }
                        if (!found) {
                            currentArray.add(newObject);
                        }
                    }
                } else {
                    configuration.add(key, newValue);
                }
            } else {
                configuration.add(key, newValue);
            }
        }
        System.out.println("Updated configuration: " + currentCarConfiguration);
        return currentCarConfiguration;
    }
}
