package com.motorist.manufacturer.restcontroller;


import java.nio.file.Files;
import java.nio.file.Path;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.motorist.securedocument.core.CryptographicOperations;

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
        JsonObject content_with_firmWare = CryptographicOperations.protectFirmware(firmware.getBytes(), content);
        return content_with_firmWare;

    }

}
