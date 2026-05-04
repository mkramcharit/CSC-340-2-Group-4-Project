package com.pcplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * PC+ Game Store – Spring Boot entry point.
 *
 * Run with:  mvn spring-boot:run
 * Build JAR: mvn clean package
 *            java -jar target/pcplus-api-1.0.0.jar
 */
@SpringBootApplication
public class PcPlusApplication {

    public static void main(String[] args) {
        SpringApplication.run(PcPlusApplication.class, args);
    }
}
