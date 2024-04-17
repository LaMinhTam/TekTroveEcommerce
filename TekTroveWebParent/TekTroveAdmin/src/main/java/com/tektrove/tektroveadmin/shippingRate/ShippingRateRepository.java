package com.tektrove.tektroveadmin.shippingRate;

import com.tektrove.tektroveadmin.paging.SearchRepository;
import com.tektrovecommon.entity.ShippingRate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;

public interface ShippingRateRepository extends SearchRepository<ShippingRate, Integer> {
    ShippingRate findByCountry_IdAndState(Integer countryId, String state);

    @Query("SELECT s FROM ShippingRate s WHERE s.country.name LIKE %?1% OR s.state LIKE %?1%")
    Page<ShippingRate> findAll(String keyword, Pageable pageable);

    public Long countById(Integer id);
}
