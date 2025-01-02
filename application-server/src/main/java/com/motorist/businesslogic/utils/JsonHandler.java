package com.motorist.businesslogic.utils;

import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

public class JsonHandler {
    

    public static JsonObject createBaseJson() {

        JsonObject jsonObject = new JsonObject();

        JsonObject content = new JsonObject();
        jsonObject.add("content", content);
        JsonObject metadata = new JsonObject();
        jsonObject.add("metadata", metadata);


        return jsonObject;
    }

    public static JsonObject addConfiguration(JsonObject json, JsonObject config) {

        JsonObject content = json.getAsJsonObject("content");
        content.addProperty("success" , "true");
        content.add("data", config);
        return json;
    }

    public static JsonObject addErrorMessage ( JsonObject json , String message){
        JsonObject content = json.getAsJsonObject("content");
        content.addProperty("success" , "false");
        content.addProperty("data" , message );
        return json;
    }

    public static JsonObject standartResponse (JsonObject json){
        JsonObject content = json.getAsJsonObject("content");
        content.addProperty("success" , "true");
        content.addProperty("data" , "Operation was successful");
        return json;
    }

    public static JsonObject responseJsonOnEncryptionError(){

        JsonObject jsonObject = createBaseJson();
        JsonObject content = jsonObject.getAsJsonObject("content");
        content.addProperty("success" , "false");
        content.addProperty("data" , "Error on encryption");
        return jsonObject;
    }

    public static JsonObject addLogs (JsonObject json , List<JsonObject> logs){
        JsonObject content = json.getAsJsonObject("content");
        content.addProperty("success" , "true");

        JsonArray logsArray = new JsonArray();
        for (JsonObject log : logs) {
            logsArray.add(log);
        }
        content.add("data" , logsArray);
        return json;
    }

}
