package com.motorist.businesslogic.restcontroller;

import java.util.Optional;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.motorist.businesslogic.service.ServiceCar;
import com.motorist.businesslogic.utils.JsonHandler;

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
            String result = serviceCar.getConfiguration(digitalSignature, password);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            System.out.println("Unexpected error !");
            return JsonHandler.responseJsonOnEncryptionError().toString();
        }
    }

    
    @PostMapping("/change")
    public String modifyConfiguration(
        @RequestBody String body)
    {
        System.out.println("Received a POST change configuration request");
        try{
            String result = serviceCar.modifyConfiguration(body);
            System.out.println(result);
            return result;
        } catch (Exception e) {
            System.out.println("Unexpected error !");
            return JsonHandler.responseJsonOnEncryptionError().toString();
        }
    }

    @PostMapping("/maintenance")
    public String setMaintenance(
        @RequestBody String body)
    {
        System.out.println("Received a POST maintenance request");
        try{
            String result =  serviceCar.setMaintenance(body);
            return result;
        } catch (Exception e) {
            System.out.println("Unexpected error !");
            return JsonHandler.responseJsonOnEncryptionError().toString();
        }
    }

    @PostMapping("/update")
    public String updateFirmware(
        @RequestBody String body)
    {
        System.out.println("Received a POST firmware request");
        try{
            String result =  serviceCar.modifyFirmware(body);
            return result;
        } catch (Exception e) {
            System.out.println("Unexpected error !");
            return JsonHandler.responseJsonOnEncryptionError().toString();
        }
    }

    @GetMapping("/view")
    public String getLogs(
        @RequestHeader("Digital-Signature") String digitalSignature)
    {
        System.out.println("Received a GET logs request");
        try{
            String result = serviceCar.getLogs(digitalSignature);
            return result;
        }
        catch(Exception e){
            System.out.println("Unexpected error !");
            return JsonHandler.responseJsonOnEncryptionError().toString();
        }

    }

}
