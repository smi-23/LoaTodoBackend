package com.loatodo.loatodobackend;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @RequestMapping("/")
    public String Hello() {
        return "sudo docker-compose down 후 재실 후 잘되는 것 확인 다시 한번 테스트";
    }
}
