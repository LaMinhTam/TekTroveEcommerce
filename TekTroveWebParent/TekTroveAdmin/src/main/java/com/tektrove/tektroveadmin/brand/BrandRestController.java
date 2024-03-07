package com.tektrove.tektroveadmin.brand;

import com.tektrove.tektroveadmin.category.CategoryDTO;
import com.tektrovecommon.entity.Brand;
import com.tektrovecommon.exception.BrandNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/brands/{id}/categories")
    public List<CategoryDTO> getCategoriesByBrand(@PathVariable Integer id) throws BrandNotFoundException {
        Brand brand = brandService.get(id);
        return brand.getCategories().stream()
                .map(category -> new CategoryDTO(category.getId(), category.getName()))
                .collect(Collectors.toList());
    }
}