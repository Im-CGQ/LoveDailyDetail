package com.lovediary.controller;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/simple")
@CrossOrigin(origins = "*")
public class SimpleTestController {

    @GetMapping("/ping")
    public String ping() {
        return "pong";
    }

    @GetMapping("/status")
    public String status() {
        return "Spring Boot is running!";
    }
}
