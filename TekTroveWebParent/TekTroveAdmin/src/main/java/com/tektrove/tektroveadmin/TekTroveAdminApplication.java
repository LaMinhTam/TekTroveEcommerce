package com.tektrove.tektroveadmin;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;

@SpringBootApplication
@EntityScan({"com.tektrovecommon.entity"})
public class TekTroveAdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(TekTroveAdminApplication.class, args);
    }

}
