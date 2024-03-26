package com.tektrove.tektroveadmin.setting.country;

import com.tektrovecommon.entity.setting.Country;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CountryService {
    private final CountryRepository countryRepository;

    public CountryService(CountryRepository countryRepository) {
        this.countryRepository = countryRepository;
    }

    public List<Country> findAllByOrderByNameAsc() {
        return countryRepository.findAllByOrderByNameAsc();
    }

    public Country save(Country country) {
        return countryRepository.save(country);
    }

    public void delete(int id) {
        countryRepository.deleteById(id);
    }
}
