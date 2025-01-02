package com.motorist.client.commands;

import org.springframework.web.client.ResourceAccessException;

import com.google.gson.JsonObject;
import com.motorist.client.communications.HTTPHandler;
import com.motorist.client.utils.JsonHandler;
import static com.motorist.securedocument.core.CryptographicOperations.addSecurity;
import static com.motorist.securedocument.core.CryptographicOperations.doCheck;
import static com.motorist.securedocument.core.CryptographicOperations.removeSecurity;
import com.motorist.securedocument.core.integrity.func.DigitalSignatureImpl;


public class UpdateCommand implements Command {
    
    private String role;

    private final String COMMAND = "update";

    public UpdateCommand(String role , String[] parts) throws IllegalArgumentException{

        if(!validateCommand(role, parts)){
            throw new IllegalArgumentException("Invalid command");
        }
        this.role = role;
    }

    @Override
    public void handleCommand(HTTPHandler handler){

        // handle command
        JsonObject response;
        String ds;
        try {
            ds = DigitalSignatureImpl.signGetRequest(COMMAND, role , 0);
            response = handler.sendGetRequest(ds, COMMAND,role , "manufacturer");
            if ( doCheck(response, "manufacturer", role , 0) ) 
                response = removeSecurity(response, role , 0);
            if ( response.get("content").getAsJsonObject().get("success").getAsString().equals("false")) {
                System.out.println("Could not get firmware update because you have no permission to do this command");
                return;
            }
            String encrypptedFirmware = response.get("content").
            getAsJsonObject().get("firmware").getAsString();
            String firmware_digest = response.get("content").
            getAsJsonObject().get("firmware_digest").getAsString();
            JsonObject payload = getPayload(encrypptedFirmware, firmware_digest);
            response = handler.sendPayload(payload, COMMAND);
            if ( doCheck(response,"server" , this.role, 0) ) 
                displayPayload(removeSecurity(response, role , 0));

        } catch (ResourceAccessException e) {
            System.out.println("Could not establish connection with server because you have no permission to do this command");
        }
        catch (Exception e){
            System.out.println("Error: " + e.getMessage());
        } 
        
    }
    
    public JsonObject getPayload(String firmware, String firmwareDigest){
        // mudar so para um get para o manufacturer
        System.out.println("Getting JSON for update command");
        JsonObject result = JsonHandler.createBaseJson(this.role, COMMAND);
        result = JsonHandler.addFirmware(result, firmware, firmwareDigest);
        JsonObject encryptedPayload = null;
        try {
            encryptedPayload = addSecurity(result, this.role, "server" , 0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return encryptedPayload;
    }

    @Override
    public void displayPayload(JsonObject response){
        System.out.println("Displaying payload for update command");
        JsonObject content = response.get("content").getAsJsonObject();
        boolean success = content.get("success").getAsBoolean();
        if (success) {
            System.out.println("Firmware updated successfully");
        } else {
            System.out.println("Firmware update failed");
            
        }
    }

    @Override
    public boolean validateCommand(String role, String[] parts){
        //validate command
        return parts.length == 2;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
    public String getCommand() {
        return COMMAND;
    }
    
}
