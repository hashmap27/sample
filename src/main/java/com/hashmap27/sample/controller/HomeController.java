package com.hashmap27.sample.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Default Home Controller
 */

@RestController
public class HomeController {

    @GetMapping("/")
    public String home() {
        return "hello world!";
    }
}
