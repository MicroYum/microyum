package com.microyum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@EntityScan(basePackages = {"com.microyum.model"})
@EnableJpaRepositories(basePackages = {"com.microyum.dao.jpa"})
@SpringBootApplication
@EnableScheduling
@EnableAsync
public class MicroYumApplication {

    public static void main(String[] args) {
        SpringApplication.run(MicroYumApplication.class, args);
    }
}
