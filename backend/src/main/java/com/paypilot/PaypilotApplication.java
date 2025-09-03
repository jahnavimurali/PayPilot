package com.paypilot;

import org.springframework.boot.SpringApplication;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling   //imp for reminder
@SpringBootApplication
public class PaypilotApplication {
    public static void main(String[] args) {
        SpringApplication.run(PaypilotApplication.class, args);
        System.out.println("Application is running..");
    }
}