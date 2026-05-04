package com.pcplus;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * PC+ Game Store – Spring Boot entry point.
 *
 * Run with:  mvn spring-boot:run
 * Build JAR: mvn clean package
 *            java -jar target/pcplus-api-1.0.0.jar
 */
@SpringBootApplication
@EntityScan("com.pcplus.model")
@EnableJpaRepositories("com.pcplus.repository")
public class PcPlusApplication {

    public static void main(String[] args) {
        SpringApplication.run(PcPlusApplication.class, args);
    }
}
