package com.loatodo.loatodobackend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/")
    public String Hello() {
        return "소셜로그인 배포 테스트 현재 redirect uri는 로컬값";
    }
}
