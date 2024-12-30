package com.motorist.businesslogic.restcontroller;

import com.google.gson.JsonObject;
import com.motorist.businesslogic.restcontroller.data.out.APIResponse;
import com.motorist.businesslogic.restcontroller.data.out.APIResponseGetLogs;
import com.motorist.businesslogic.service.ServiceCar;
import com.motorist.businesslogic.service.errors.CarConfigurationNotFoundException;
import com.motorist.businesslogic.service.errors.FirmwareNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
    public String getConfiguration(
        @RequestHeader("Digital-Signature") String digitalSignature,
        @RequestHeader(value = "Password", required = false) Optional<String> password)
    {
        System.out.println("Received a GET configuration request");
        System.out.println("This is the header received: " + digitalSignature);
        try{
            return serviceCar.getConfiguration(digitalSignature, password);
        } catch (Exception e) {
            return new String();
        }
    }

    @PostMapping("/change")
    public String modifyConfiguration(
        //@RequestHeader("X-Digital-Signature") String digitalSignature
        @RequestBody String body)
    {
        System.out.println("Received a POST change configuration request");
        try{
            return serviceCar.modifyConfiguration(body));
        } catch (CarConfigurationNotFoundException e) {
            return new String();
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

    @GetMapping("/view")
    public APIResponseGetLogs getLogs(
        @RequestHeader("X-Digital-Signature") String digitalSignature,
        @RequestHeader(value = "Password", required = false) Optional<String> password)
    {
        System.out.println("Received a GET logs request");
        try{
            return new APIResponseGetLogs(true, serviceCar.getLogs(digitalSignature , password));
        }
        catch(Exception e){
            //
        }

    }
}
