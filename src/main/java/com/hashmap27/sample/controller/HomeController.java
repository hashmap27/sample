package com.hashmap27.sample.controller;

import com.hashmap27.sample.domain.vo.HomeVO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Default Home Controller
 */

@RestController
public class HomeController {

    @GetMapping("/")
    public HomeVO home() {
        return HomeVO.builder()
                .message("hello world!").build();
    }
}
