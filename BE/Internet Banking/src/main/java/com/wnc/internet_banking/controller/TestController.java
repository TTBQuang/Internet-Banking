package com.wnc.internet_banking.controller;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {
    // This is a test controller to verify the application is running
    // You can add your test endpoints here

    // Example endpoint
    @GetMapping("/test")
    public String test() {
        return "Test endpoint is working!";
    }
}
