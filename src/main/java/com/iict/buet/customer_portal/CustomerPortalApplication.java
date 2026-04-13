package com.iict.buet.customer_portal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class CustomerPortalApplication {

    public static void main(String[] args) {
        SpringApplication.run(CustomerPortalApplication.class, args);
    }

}
