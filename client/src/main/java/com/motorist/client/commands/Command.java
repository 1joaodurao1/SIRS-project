package com.motorist.client.commands;

import com.google.gson.JsonObject;
import com.motorist.client.communications.HTTPHandler;

public interface Command {
    
    public void handleCommand(HTTPHandler handler);
    public void displayPayload(JsonObject response);
    public boolean validateCommand(String role, String[] parts);
}
