package com.motorist.client.commands;

import com.google.gson.JsonObject;
import com.motorist.client.communications.HTTPHandler;
import com.motorist.client.utils.JsonHandler;
import static com.motorist.securedocument.core.CryptographicOperations.addSecurity;
import static com.motorist.securedocument.core.CryptographicOperations.doCheck;
import static com.motorist.securedocument.core.CryptographicOperations.removeSecurity;


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
        JsonObject payload = getPayload();
        JsonObject response = handler.sendPayload(payload, COMMAND);
        try {
            if ( doCheck(response, "server", role , 0) ) displayPayload(removeSecurity(response, role , 0));
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }
    
    public JsonObject getPayload(){

        System.out.println("Getting JSON for update command");
        JsonObject result = JsonHandler.createBaseJson(this.role, COMMAND);

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
        System.out.println(response);
    }

    @Override
    public boolean validateCommand(String role, String[] parts){
        //validate command
        return parts.length != 2;
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
