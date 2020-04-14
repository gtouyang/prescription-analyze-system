package com.ogic.prescriptionsyntheticsystem;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class PrescriptionSyntheticSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(PrescriptionSyntheticSystemApplication.class, args);
    }

}
