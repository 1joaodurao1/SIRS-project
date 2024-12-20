package com.motorist.businesslogic.restcontroller;

import com.motorist.businesslogic.service.ServiceCar;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/car")
public class RestService {

    private ServiceCar serviceCar;

    public RestService (
        final ServiceCar serviceCar)
    {
            this.serviceCar = serviceCar;
    }

    @GetMapping("/configuration")
    public String getConfiguration() {
        return "Here is the configuration: ( .  人  . )";
    }

    @PutMapping("/configuration")
    public String modifyConfiguration() {
        return "Configuration modified!";
    }

    @PutMapping("/firmware")
    public String updateFirmware() {
        return "Firmware modified";
    }

    @GetMapping("/logs")
    public String getLogs() {
        return "Here are the logs";
    }
}
