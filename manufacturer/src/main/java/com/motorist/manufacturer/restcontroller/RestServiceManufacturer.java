package com.motorist.manufacturer.restcontroller;


import com.motorist.manufacturer.restcontroller.data.APIResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

@RestController
@RequestMapping("api/manufacturer")
public class RestServiceManufacturer {

    private static final String FIRMWARE_LOCATION = "firmware.txt";


    public RestServiceManufacturer() {}

    @GetMapping("/firmware")
    public APIResponse getConfiguration(
        //@RequestHeader("X-Digital-Signature") String digitalSignature
    )
    {
        try {
            System.out.println("Received a GET firmware request");
            Path file = Path.of(FIRMWARE_LOCATION);
            String firmware = Files.readString(file);
            return new APIResponse(true, firmware);
        } catch (IOException e) {
            return new APIResponse(false, "Error while fetching firmware: Firmware doesn't exist.");
        }
    }
}
