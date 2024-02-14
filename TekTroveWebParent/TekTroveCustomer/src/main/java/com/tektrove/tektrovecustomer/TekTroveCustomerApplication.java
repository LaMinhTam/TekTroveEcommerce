package com.tektrove.tektrovecustomer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.tektrovecommon.entity"})
public class TekTroveCustomerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TekTroveCustomerApplication.class, args);
    }

}
