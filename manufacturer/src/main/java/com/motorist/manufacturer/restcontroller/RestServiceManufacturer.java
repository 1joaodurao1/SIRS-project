package com.motorist.manufacturer.restcontroller;


import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.motorist.securedocument.core.CryptographicOperations;
import com.motorist.securedocument.core.integrity.func.DigitalSignatureImpl;


@RestController
@RequestMapping("api/manufacturer")
public class RestServiceManufacturer {

    private static final String FIRMWARE_LOCATION = "firmware.txt";


    public RestServiceManufacturer() {}

    @GetMapping("/update")
    public String getConfiguration(
        @RequestHeader("Digital-Signature") String digitalSignature
    )
    {
        try {
            JsonObject jsonObject = createBaseJson();
            ArrayList<String> accessControlList = new ArrayList<>(Arrays.asList(  "mechanic"));
            String sender = hasAccess(digitalSignature);
            if ( accessControlList.contains(sender) == false) {
                jsonObject = addErrorMessage(jsonObject, "You are not authorized to access this.");
                return CryptographicOperations.addSecurity(jsonObject, "manufacturer", sender , 4).toString();
            }
            Path file = Path.of(FIRMWARE_LOCATION);
            JsonObject content = jsonObject.get("content").getAsJsonObject();
            String firmware = Files.readString(file);
            content = addFirmware(jsonObject, firmware);
            jsonObject.add("content", content);
            String result = CryptographicOperations.addSecurity(jsonObject, "manufacturer", "mechanic", 4).toString();
            return result;
        } catch (Exception e) {
            System.out.println("Unexpected error !" + e.getMessage());
            return new String();
        }

        
    }

    public static JsonObject createBaseJson() {

        JsonObject jsonObject = new JsonObject();

        JsonObject content = new JsonObject();
        jsonObject.add("content", content);
        JsonObject metadata = new JsonObject();
        jsonObject.add("metadata", metadata);


        return jsonObject;
    }

    public static JsonObject addFirmware(JsonObject jsonObject, String firmware) throws Exception {
        System.out.println("Adding firmware to JSON");
        JsonObject content = jsonObject.get("content").getAsJsonObject();
        content.addProperty("success", "true");
        JsonObject content_with_firmWare = CryptographicOperations.protectFirmware(firmware.getBytes(), content);
        return content_with_firmWare;

    }

    private static String hasAccess ( String ds) throws Exception{
        ArrayList<String> possibleUsers = new ArrayList<>(Arrays.asList( "owner", "user", "mechanic"));
        for ( String user : possibleUsers){
            if (DigitalSignatureImpl.checkGetRequest("update", user,ds , 4)) {
                return user;
            }
                    
        }
       return "";
    }

    public static JsonObject addErrorMessage ( JsonObject json , String message){
        JsonObject content = json.getAsJsonObject("content");
        content.addProperty("success" , "false");
        content.addProperty("data" , message );
        return json;
    }

}
