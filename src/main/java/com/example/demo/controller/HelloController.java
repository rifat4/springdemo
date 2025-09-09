package com.example.demo.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @Value("${app.version}")
    private String version;

    @GetMapping("/")
    public String index() {
        return "Test1 from " + version;
    }
}