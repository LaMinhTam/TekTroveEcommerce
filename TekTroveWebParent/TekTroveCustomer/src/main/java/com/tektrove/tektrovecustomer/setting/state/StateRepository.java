package com.tektrove.tektrovecustomer.setting.state;

import com.tektrovecommon.entity.setting.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateRepository extends JpaRepository<State, Integer> {
    List<State> findByCountry_IdOrderByNameAsc(Integer countryId);
}
