package com.motorist.businesslogic.restcontroller;

import com.motorist.businesslogic.restcontroller.data.APIResponse;
import com.motorist.businesslogic.restcontroller.data.APIResponseLogs;
import com.motorist.businesslogic.service.ServiceCar;
import com.motorist.businesslogic.service.errors.CarConfigurationNotFoundException;
import com.motorist.businesslogic.service.errors.FirmwareNotFoundException;
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
    public APIResponse getConfiguration()
    {
        System.out.println("Received a GET configuration request");
        try{
            return new APIResponse(true, serviceCar.getConfiguration());
        } catch (CarConfigurationNotFoundException e) {
            return new APIResponse(false, "Error while fetching configuration: " + e.getMessage());
        }
    }

    @PutMapping("/configuration")
    public APIResponse modifyConfiguration(
        @RequestBody String body)
    {
        System.out.println("Received a PUT configuration request");
        try{
            return new APIResponse(true, serviceCar.modifyConfiguration(body));
        } catch (CarConfigurationNotFoundException e) {
            return new APIResponse(false,"Error while updating configuration: " + e.getMessage());
        }
    }

    @PutMapping("/firmware")
    public APIResponse updateFirmware(
        @RequestHeader("X-Digital-Signature") String digitalSignature)
    {
        System.out.println("Received a PUT firmware request");
        try{
            return new APIResponse(true, serviceCar.modifyFirmware());
        } catch (FirmwareNotFoundException e) {
            return new APIResponse(false, "Error while updating firmware: " + e.getMessage());
        }
    }

    @GetMapping("/logs")
    public APIResponseLogs getLogs(
        @RequestHeader("X-Digital-Signature") String digitalSignature)
    {
        System.out.println("Received a GET logs request");
        return new APIResponseLogs(true, serviceCar.getLogs());
    }
}
