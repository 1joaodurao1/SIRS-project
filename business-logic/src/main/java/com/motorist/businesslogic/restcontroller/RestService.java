package com.motorist.businesslogic.restcontroller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/car")
public class RestService {

    @GetMapping("/test")
    public String testEndpoint() {
        return "SSL server is working!";
    }
}
