package com.motorist.businesslogic.restcontroller;

import com.google.gson.JsonObject;
import com.motorist.businesslogic.service.ServiceCar;
import com.motorist.businesslogic.service.errors.CarConfigurationNotFoundException;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/car")
public class RestService {

    private final ServiceCar serviceCar;

    public RestService (
        final ServiceCar serviceCar)
    {
            this.serviceCar = serviceCar;
    }

    @GetMapping("/configuration")
    public String getConfiguration()
    {
        try{
            return serviceCar.getConfiguration();
        } catch (CarConfigurationNotFoundException e) {
            return "Error: " + e.getMessage();
        }
    }

    @PutMapping("/configuration")
    public String modifyConfiguration(
        @RequestBody String body)
    {
        try{
            return serviceCar.modifyConfiguration(body.toString());
        } catch (CarConfigurationNotFoundException e) {
            return "Error: " + e.getMessage();
        }
    }

    @PutMapping("/firmware")
    public String updateFirmware(
        @RequestHeader("X-Digital-Signature") String digitalSignature)
    {
        return "Firmware modified";
    }

    @GetMapping("/logs")
    public String getLogs(
        @RequestHeader("X-Digital-Signature") String digitalSignature)
    {
        return "Here are the logs";
    }
}
