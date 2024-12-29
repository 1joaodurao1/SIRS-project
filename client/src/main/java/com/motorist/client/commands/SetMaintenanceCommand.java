package com.motorist.client.commands;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.net.ssl.SSLHandshakeException;

import com.google.gson.JsonObject;
import com.motorist.client.communications.HTTPHandler;
import com.motorist.client.utils.JsonHandler;
import static com.motorist.securedocument.core.CryptographicOperations.addSecurity;
import static com.motorist.securedocument.core.CryptographicOperations.doCheck;
import static com.motorist.securedocument.core.CryptographicOperations.removeSecurity;



public class SetMaintenanceCommand implements Command {
    
    private String role;

    private final String password;

    private final String COMMAND = "maintenance";

    private boolean isOn = false;

    public SetMaintenanceCommand(String role, String[] parts) throws IllegalArgumentException{

        if(!validateCommand(role, parts)){
            throw new IllegalArgumentException("Invalid command");
        }
        this.role = role;
        this.isOn = parts[2].equals("on");
        this.password = parts[3];
        
    }

    @Override
    public void handleCommand(HTTPHandler handler){

        // handle command
        JsonObject payload = getPayload();
        JsonObject response;
    
        try {
            response = handler.sendPayload(payload, COMMAND);
            if ( doCheck(response,"server" , this.role , 0) ) displayPayload(removeSecurity(response, role, 0));
        }
        catch (SSLHandshakeException e){
            System.out.println("Could not establish connection with server");
        }  
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public JsonObject getPayload(){
        
        System.out.println("Getting JSON for view command");
        JsonObject result = JsonHandler.createBaseJson(this.role, COMMAND);
        result = JsonHandler.addMaintenanceMode(result, this.isOn , this.password );
        
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
        System.out.println("Displaying payload for view command");

        JsonObject content = response.getAsJsonObject("content");
        Boolean success = content.get("success").getAsBoolean();
        if (success){
            System.out.println("Maintenance mode is now " + (this.isOn ? "on" : "off"));
            if ( this.isOn ){
                storePassword(this.password);
            } else {
                removePassword();
            }
        } else {
            System.out.println("Failed to change maintenance mode");
        }
        //depending on the response if the request was for turning off the maintenace mode and it was successfull then the password will be removed
        //storePassword(this.password);
        //removePassword();
        System.out.println(response);
    }

    @Override
    public boolean validateCommand(String role, String[] parts){

        return parts.length == 4 && (parts[2].equals("on")
         || parts[2].equals("off"));
    
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

    private void storePassword(String password) {
        // store password

        // create the file if it does not exist
        if (this.isOn){
            String path = System.getProperty("user.dir") + "/client/src/main/password.txt";
            Path filePath = Paths.get(path);

            if ( !Files.exists(filePath) ){
                // create the file and write the password
                try {
                    Files.createFile(filePath);
                    String passwordHash = hash(password);
                    Files.write(filePath , passwordHash.getBytes(StandardCharsets.UTF_8), StandardOpenOption.TRUNCATE_EXISTING);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }
    }

    private void removePassword() {
        // remove password
        if ( !this.isOn ){
            String path = System.getProperty("user.dir") + "/client/src/main/password.txt";
            Path filePath = Paths.get(path);

            if ( Files.exists(filePath) ){
                // remove the file
                try {
                    Files.delete(filePath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private String hash(String password) {
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
