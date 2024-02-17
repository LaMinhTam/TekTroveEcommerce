package com.tektrove.tektroveadmin.brand;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BrandRestController {

    private final BrandService brandService;

    @Autowired
    public BrandRestController(BrandService brandService) {
        this.brandService = brandService;
    }

    @GetMapping("/brands/check-unique")
    public String checkUniqueBrandName(@RequestParam Integer id, @RequestParam String name) {
        return brandService.isBrandNameUnique(id, name) ? "OK" : "Duplicate";
    }
}