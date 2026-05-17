package org.bazar.bazarstorage.fw;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "org.bazar.bazarstorage")
public class BazarStorageApplication {
    public static void main(String[] args) {
        SpringApplication.run(BazarStorageApplication.class, args);
    }
}
