package com.loatodo.loatodobackend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/")
    public String Hello() {
        return "재실행 후 재 커밋 테스트";
    }
}