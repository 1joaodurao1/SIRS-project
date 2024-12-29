package com.motorist.client.commands;

import javax.net.ssl.SSLHandshakeException;

import com.google.gson.JsonObject;
import com.motorist.client.communications.HTTPHandler;
import static com.motorist.securedocument.core.CryptographicOperations.doCheck;
import static com.motorist.securedocument.core.CryptographicOperations.removeSecurity;
import com.motorist.securedocument.core.integrity.func.DigitalSignatureImpl;


public class ViewLogsCommand implements Command {
    
    private String role;

    private final String COMMAND = "view";

    public ViewLogsCommand(String role , String[] parts) throws IllegalArgumentException{

        if(!validateCommand(role, parts)){
            throw new IllegalArgumentException("Invalid command");
        }
        this.role = role;
    }

    @Override
    public void handleCommand(HTTPHandler handler){

        // handle command
        String ds ;
        try {
            // Encrypt the response with the role key
            ds = DigitalSignatureImpl.signGetRequest(COMMAND, role , 0);
            JsonObject response = handler.sendGetRequest(ds, COMMAND,role);
            if ( doCheck(response,"server" , this.role , 0) ) displayPayload(removeSecurity(response, role , 0));

        } 
        catch (SSLHandshakeException e){
            System.out.println("Could not establish connection with server");
        } 
        catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void displayPayload(JsonObject response){
        System.out.println("Displaying payload for view command");
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
