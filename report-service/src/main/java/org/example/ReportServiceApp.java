package org.example;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients(basePackages = "org.example.service")
@SpringBootApplication
public class ReportServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(ReportServiceApp.class, args);
    }
}