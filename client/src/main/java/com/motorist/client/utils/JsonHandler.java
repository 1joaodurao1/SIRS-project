package com.motorist.client.utils;


import com.google.gson.JsonObject;

public class JsonHandler {

    public static JsonObject createBaseJson(String role,  String command) {

        JsonObject jsonObject = new JsonObject();

        JsonObject content = new JsonObject();
        content.addProperty("command", command);
        content.addProperty("role", role);
        jsonObject.add("content", content);
        JsonObject metadata = new JsonObject();
        jsonObject.add("metadata", metadata);


        return jsonObject;
    }

    public static JsonObject addConfigChanges(JsonObject jsonObject, String[] changes){
        // add the changes to the jsonObject
        
        JsonObject content = jsonObject.get("content").getAsJsonObject();

        JsonObject configurations = new JsonObject();
        
        for ( int i = 2 ; i < changes.length ; i++){
            String[] elements = changes[i].split(":");
            String config = decipherConfig(elements[0], "ac");
            if ( config != null){
                if ( configurations.getAsJsonObject("ac") == null){
                    JsonObject ac = new JsonObject();
                    ac.addProperty(config, elements[1]);
                    configurations.add("ac", ac);
                }
                else{
                    configurations.getAsJsonObject("ac").addProperty(config, elements[1]);
                }
            }else {
                config = decipherConfig(elements[0], "seat");
                if ( content.getAsJsonObject("seat") == null){
                    JsonObject seat = new JsonObject();
                    seat.addProperty(config, elements[1]);
                    configurations.add("seat", seat);
                }
                else{
                    configurations.getAsJsonObject("seat").addProperty(config, elements[1]);
                }
            }
        }
        content.add("configurations", configurations);

        return jsonObject;
    }

    public static JsonObject addMaintenanceMode (JsonObject jsonObject, boolean mode , String password){
        // add the maintenance mode to the jsonObject
        JsonObject content = jsonObject.get("content").getAsJsonObject();
        content.addProperty("maintenance_mode", mode);
        content.addProperty("password", password); // doubt if i should send password or hash of it

        return jsonObject;
    }

    public static String decipherConfig (String config , String pattern){

        if (config.contains(pattern)){
            System.out.println("Deciphering config: " + config);
            String[] elements = config.split("_");
            return elements[1];
        }
        else{
            return null;
        }
    
    }

    public static JsonObject addPassword (JsonObject jsonObject, String role){
        // add the password to the jsonObject
        if ( role.equals("owner") ) {
            String hash_password = Common.checkPassword();
            if ( hash_password != null ) {
                JsonObject content = jsonObject.get("content").getAsJsonObject();
                content.addProperty("password", hash_password);
            }

        }
        return jsonObject;
    }
    
    
}
