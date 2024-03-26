package com.tektrove.tektroveadmin.setting.country;

import com.tektrovecommon.entity.setting.Country;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
public class CountryRestController {
    private final CountryService countryService;

    public CountryRestController(CountryService countryService) {
        this.countryService = countryService;
    }

    @GetMapping()
    public List<Country> findAllByOrderByNameAsc() {
        return countryService.findAllByOrderByNameAsc();
    }

    @PostMapping("/save")
    public Country save(@RequestBody Country country) {
        return countryService.save(country);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") int id) {
        countryService.delete(id);
    }
}
