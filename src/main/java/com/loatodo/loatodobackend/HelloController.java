package com.loatodo.loatodobackend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/")
    public String Hello() {
        return "http 배포 테스트 중입니다.6/23 %불포함 db ip id도 다른거 적용 일단 배포는 됨 바로 종료가 되기 때문에 로그 확인2 ";
    }
}
