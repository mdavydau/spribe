package com.mdavydau.spribe;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class SpribeApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpribeApplication.class, args);
    }

}
