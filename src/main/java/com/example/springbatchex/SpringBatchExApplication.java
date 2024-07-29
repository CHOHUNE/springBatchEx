package com.example.springbatchex;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SpringBatchExApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBatchExApplication.class, args);
    }

}
