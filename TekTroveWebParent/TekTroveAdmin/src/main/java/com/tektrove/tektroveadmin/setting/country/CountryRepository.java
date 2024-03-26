package com.tektrove.tektroveadmin.setting.country;

import com.tektrovecommon.entity.setting.Country;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CountryRepository extends JpaRepository<Country, Integer> {
    public List<Country> findAllByOrderByNameAsc();

}
