package com.motorist.client.commands;

import com.google.gson.JsonObject;
import com.motorist.client.communications.HTTPHandler;
import static com.motorist.securedocument.core.CryptographicOperations.doCheck;
import static com.motorist.securedocument.core.CryptographicOperations.removeSecurity;
import com.motorist.securedocument.core.integrity.func.DigitalSignatureImpl;


public class ReadCommand implements Command {
    
    private String role;

    private boolean useOwnerKey = false;

    private final String COMMAND = "read";

    public ReadCommand(String role, String[] parts) throws IllegalArgumentException{

        if ( ! validateCommand(role, parts) ){
            throw new IllegalArgumentException("Invalid command");
        }
        this.role = role;
        this.useOwnerKey = isMechanicUsingOwnerKey(parts);
    }

    @Override
    public void handleCommand(HTTPHandler handler){

        // handle command
        String ds ;
        try {
            if (useOwnerKey){
                // Encrypt the response with the owner key
                ds = DigitalSignatureImpl.signGetRequest(COMMAND, "owner" , 0);
            } else {
                // Encrypt the response with the role key
                ds = DigitalSignatureImpl.signGetRequest(COMMAND, role , 0);
            }
            JsonObject response = handler.sendGetRequest(ds, COMMAND);
            if ( doCheck(response,"server" , this.role , 0) ) displayPayload(removeSecurity(response, role , 0));

        } catch (Exception e) {
            e.printStackTrace();
        }
    
    }


    @Override  
    public void displayPayload(JsonObject response){
        System.out.println("Displaying payload for read command");
        System.out.println(response);
    }

    @Override
    public boolean validateCommand(String role, String[] parts){
        
        if ( parts.length == 2 && role.equals("mechanic")){
            return false;
        }

        if (role.equals("mechanic") && parts.length == 3){
            if ( ! parts[2].equals("yes") && ! parts[2].equals("no")){
                return false;
            }
        }
    
        return true;
    }

    private boolean isMechanicUsingOwnerKey(String[] parts){
        return parts.length == 3 && parts[2].equals("yes");
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
