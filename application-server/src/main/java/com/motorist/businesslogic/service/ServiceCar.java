package com.motorist.businesslogic.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.motorist.businesslogic.domain.EntityCarAudit;
import com.motorist.businesslogic.domain.EntityCarConfiguration;
import com.motorist.businesslogic.repository.RepositoryCarAudit;
import com.motorist.businesslogic.repository.RepositoryCarConfiguration;
import com.motorist.businesslogic.service.errors.CarConfigurationNotFoundException;
import com.motorist.businesslogic.service.errors.FirmwareNotFoundException;
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
        JsonObject response = createBaseJson();
        ArrayList<String> accessControlList = new ArrayList<>(Arrays.asList( "owner"));
        String sender = hasAccess("read", digitalSignature , null);
        if ( ! accessControlList.contains(sender)) {
            response = addErrorMessage(response, "You are not authorized to access this.");
            return addSecurity(response, "server", sender , 3).toString();
        }

        final List<EntityCarConfiguration> result = repositoryCarConfiguration.findAll();
        if (result.size() != 1) { // Check this later
            response = addErrorMessage(response, "Car configuration not found");
            return addSecurity(response, "server", sender , 3).toString();
        }
        
        byte[] symmetric_key = readFileBytes(String.valueOf(getClass().getResource("/DBinitializationvalues/serverSecret.key")));
        byte[] iv = readFileBytes(String.valueOf(getClass().getResource("/DBinitializationvalues/iv.bytes")));

        JsonObject carConfiguration = SymmetricCipherImpl.decryptDB(result.get(0).getCarConfiguration(),symmetric_key,iv);
        response = addConfiguration(response, carConfiguration);
        return addSecurity(response, "server", sender , 3).toString();

    }

    public String modifyConfiguration(
        final String carConfiguration) throws Exception
    {
        JsonObject requestBody = JsonParser.parseString(carConfiguration).getAsJsonObject();

        JsonObject response = createBaseJson();
        ArrayList<String> accessControlList = new ArrayList<>(Arrays.asList( "owner"));
        String sender = hasAccess("change",null, requestBody);
        if ( ! accessControlList.contains(sender) || sender.isBlank()) {
            response = addErrorMessage(response, "You are not authorized to access this.");
            return addSecurity(response, "server", sender , 3).toString();
        }

        JsonObject content  = removeSecurity(requestBody, "server" , 3)
            .getAsJsonObject("content");

        //Depois ajustar com base no Json que recebemos
        final List<EntityCarConfiguration> result = repositoryCarConfiguration.findAll();
        if (result.size() != 1) { // Check this later
            throw new CarConfigurationNotFoundException();
        }

        byte[] symmetric_key = readFileBytes(String.valueOf(getClass().getResource("/DBinitializationvalues/serverSecret.key")));
        byte[] iv = readFileBytes(String.valueOf(getClass().getResource("/DBinitializationvalues/iv.bytes")));
    
        JsonObject currentCarConfiguration = SymmetricCipherImpl.decryptDB(result.get(0).getCarConfiguration(),symmetric_key,iv);

        
        final JsonObject changes = content.getAsJsonObject("configurations");

        for (Map.Entry<String, JsonElement> entry : changes.entrySet()) {
            String key = entry.getKey();
            JsonElement newValue = entry.getValue();

            // Update the field if it exists in the currentCarConfiguration
            if (currentCarConfiguration.getAsJsonObject("configuration").has(key)) {
                currentCarConfiguration.getAsJsonObject("configuration").add(key, newValue);
            }
        }

        final String updatedConfiguration = currentCarConfiguration.toString();

        result.get(0).setCarConfiguration(updatedConfiguration);
        repositoryCarConfiguration.save(result.get(0));
        repositoryCarAudit.save(new EntityCarAudit("A user just modified the car configuration with the following: " + carConfiguration));
        return "Car configuration successfully modified:\n" + updatedConfiguration;
    }

    public String modifyFirmware(
        final String body) throws FirmwareNotFoundException
    {
        try{
            System.out.println("Here is the body: " + body);
            Path file = Path.of(FIRMWARE_LOCATION);
            Files.writeString(file, body);
        } catch (Exception e) {
            throw new FirmwareNotFoundException();
        }
        repositoryCarAudit.save(new EntityCarAudit("A user just modified the car firmware with the following: " + body));
        return "Firmware was successfully updated!";
    }

    public List<String> getLogs(final String digitalSignature,
                                final Optional<String> password) throws Exception {
        return repositoryCarAudit
            .findAll()
            .stream()
            .map(EntityCarAudit::getActionLog)
            .toList();
    }

    // -----------------Private methods--------------------

    private static String hasAccess (  String command , String ds, JsonObject requestBody) throws Exception{
        ArrayList<String> possibleUsers = new ArrayList<>(Arrays.asList( "owner", "user", "mechanic"));
        for ( String user : possibleUsers){
            switch(command) {
                case "read":
                case "view":
                    if (DigitalSignatureImpl.checkGetRequest(command, user,ds , 2)) {
                        return user;
                    }
                    break;
                default:
                    if ( doCheck(requestBody, user , "server", 3)){
                        return user;
                    }
            }
        }
       return "";
    }

    private static JsonObject createBaseJson() {

        JsonObject jsonObject = new JsonObject();

        JsonObject content = new JsonObject();
        jsonObject.add("content", content);
        JsonObject metadata = new JsonObject();
        jsonObject.add("metadata", metadata);


        return jsonObject;
    }
    private static JsonObject addConfiguration(JsonObject json, JsonObject config) {

        JsonObject content = json.getAsJsonObject("content");
        content.addProperty("success" , "true");
        content.add("data", config);
        return json;
    }

    private static JsonObject addErrorMessage ( JsonObject json , String message){
        JsonObject content = json.getAsJsonObject("content");
        content.addProperty("success" , "false");
        content.addProperty("data" , message );
        return json;
    }

    private static byte[] readFileBytes(String filename) throws IOException {
        try (FileInputStream fis = new FileInputStream(filename)) {
            byte[] fileBytes = new byte[fis.available()];
            fis.read(fileBytes);
            return fileBytes;
        }
    }
}
