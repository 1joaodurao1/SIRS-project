package com.motorist.businesslogic.restcontroller;

import com.motorist.businesslogic.restcontroller.data.out.APIResponse;
import com.motorist.businesslogic.restcontroller.data.out.APIResponseLogs;
import com.motorist.businesslogic.service.ServiceCar;
import com.motorist.businesslogic.service.errors.CarConfigurationNotFoundException;
import com.motorist.businesslogic.service.errors.FirmwareNotFoundException;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/car")
public class RestServiceApplication {

    private final ServiceCar serviceCar;

    public RestServiceApplication(
        final ServiceCar serviceCar)
    {
            this.serviceCar = serviceCar;
    }

    @GetMapping("/read")
    public ResponseEntity<String> getConfiguration(
        @RequestHeader("Digital-Signature") String digitalSignature)
    {
        System.out.println("Received a GET configuration request");
        System.out.println("This is the header received: " + digitalSignature);
        try{
            return new ResponseEntity<>(serviceCar.getConfiguration(), HttpStatusCode.valueOf(200));
        } catch (CarConfigurationNotFoundException e) {
            return new ResponseEntity<>("Error while fetching configuration: " + e.getMessage(), HttpStatusCode.valueOf(404));
        }
    }

    @PutMapping("/configuration")
    public APIResponse modifyConfiguration(
        //@RequestHeader("X-Digital-Signature") String digitalSignature
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
        //@RequestHeader("X-Digital-Signature") String digitalSignature,
        @RequestBody String body)
    {
        System.out.println("Received a PUT firmware request");
        try{
            return new APIResponse(true, serviceCar.modifyFirmware(body));
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
