package com.loatodo.loatodobackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class LoaTodoBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LoaTodoBackendApplication.class, args);
	}

}
