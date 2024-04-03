package com.tektrove.tektroveadmin.customer;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomerRestController {
    private final CustomerService customerService;

    public CustomerRestController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @PostMapping("/customers/check_unique_email")
    public ResponseEntity<?> checkDuplicateEmail(Integer id, String email) {
        return ResponseEntity.ok(customerService.isEmailUnique(id, email));
    }
}
