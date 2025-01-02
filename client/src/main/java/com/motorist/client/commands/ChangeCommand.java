package com.motorist.client.commands;

import java.util.Arrays;

import javax.net.ssl.SSLHandshakeException;

import com.google.gson.JsonObject;
import com.motorist.client.communications.HTTPHandler;
import com.motorist.client.utils.JsonHandler;
import static com.motorist.securedocument.core.CryptographicOperations.addSecurity;
import static com.motorist.securedocument.core.CryptographicOperations.doCheck;
import static com.motorist.securedocument.core.CryptographicOperations.removeSecurity;



public class ChangeCommand implements Command {
    
    private String role;
    private String[] changes;
    private final String COMMAND = "change";
    private final String[] VALID_CONFIGS = {"ac_out1", "ac_out2", "ac_out3", "ac_out3", "ac_out4", 
                                        "seat_pos1", "seat_pos2", "seat_pos3", "seat_pos4"};

    public ChangeCommand(String role, String[] changes) throws IllegalArgumentException{

        if(!validateCommand(role, changes)){
            throw new IllegalArgumentException("Invalid command");
        }
        this.role = role;
        this.changes = changes;
    }

    @Override
    public void handleCommand(HTTPHandler handler){

        // handle command
        JsonObject payload = getPayload();
        JsonObject response;
        
        try {
            response = handler.sendPayload(payload, COMMAND);
            if ( doCheck(response,"server" , this.role, 0) ) displayPayload(removeSecurity(response, role , 0));
        }
        catch (SSLHandshakeException e){
            System.out.println("Could not establish connection with server");
        } 
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public JsonObject getPayload(){

        // handle changes array
        JsonObject result = JsonHandler.createBaseJson(this.role, COMMAND);
        result = JsonHandler.addPassword(result, role);
        result = JsonHandler.addConfigChanges(result, this.changes);
    
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
        System.out.println("Displaying payload for change command");
        boolean success = response.get("content").getAsJsonObject().get("success").getAsBoolean();
        if (success){
            System.out.println("Changes were successful");
        } else {
            String error = response.get("content").getAsJsonObject().get("data").getAsString();
            System.out.println("Error: " + error);
        }
        
    }

    @Override
    public boolean validateCommand(String role, String[] changes){
        //validate command
        // if parts lenght is less or equal than 2, return false
        if(changes.length <= 2){
            return false;
        }
        for ( int i = 2; i < changes.length; i++){
            if ( ! changes[i].contains(":")){
                return false;
            }
            String[] elements = changes[i].split(":");
            if ( elements.length != 2){
                return false;
            }
            if ( ! Arrays.asList(VALID_CONFIGS).contains(elements[0]) ) {
                listConfigs();
                return false;
            }
        }
        return true;
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
    public String[] getVALID_CONFIGS() {
        return VALID_CONFIGS;
    }

    public void listConfigs(){
        System.out.println("List of valid configurations:");
        for (String config : VALID_CONFIGS){
            System.out.println(config);
        }
    }
}
