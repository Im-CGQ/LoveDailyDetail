package com.lovediary.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
@CrossOrigin(origins = "*")
public class TestController {

    @GetMapping("/health")
    public String health() {
        return "Backend is running!";
    }

    @GetMapping("/system-config-test")
    public String systemConfigTest() {
        return "SystemConfig endpoints should be available at /system-config/*";
    }
}
