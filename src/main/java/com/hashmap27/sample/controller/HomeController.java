package com.hashmap27.sample.controller;

import com.hashmap27.sample.component.exception.SampleBadRequestException;
import com.hashmap27.sample.component.exception.SampleConflictException;
import com.hashmap27.sample.component.exception.SampleNotFoundException;
import com.hashmap27.sample.domain.vo.HomeVO;
import com.hashmap27.sample.service.HomeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Default Home Controller
 */

@Slf4j
@RequiredArgsConstructor
@RestController
public class HomeController {

    /** HomeService */
    private final HomeService homeService;

    @GetMapping("/")
    public HomeVO home() {
        return HomeVO.builder()
                .message("hello world!").build();
    }

    @GetMapping("/bad-request")
    public void badRequest() {
        throw new SampleBadRequestException("bad.request.error", "badRequest 테스트", "HomeController badRequest() 실행");
    }

    @GetMapping("/not-found")
    public void notFound() {
        throw new SampleNotFoundException("not.found.error", "notFound 테스트", "HomeController notFound() 실행");
    }

    @GetMapping("/conflict")
    public void conflict() {
        throw new SampleConflictException("conflict.error", "conflict 테스트", "HomeController conflict() 실행");
    }

    @GetMapping("/read-only")
    public Integer readOnly() {
        return this.homeService.getReadOnly();
    }

    @GetMapping("/read-write")
    public Integer readWrite() {
        return this.homeService.getReadWrite();
    }
}
