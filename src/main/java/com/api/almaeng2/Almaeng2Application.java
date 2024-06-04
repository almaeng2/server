package com.api.almaeng2;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class Almaeng2Application {

    public static void main(String[] args) {
        SpringApplication.run(Almaeng2Application.class, args);
    }

}
