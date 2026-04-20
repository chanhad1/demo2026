package com.example.demo2026.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class WelcomeController {
    @GetMapping("/Welcome")
    public String Welcome() {
        return "greeting";  
    }
}

