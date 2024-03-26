package com.tektrove.tektroveadmin.setting.state;

import com.tektrovecommon.entity.setting.State;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StateRepository extends JpaRepository<State, Integer> {
    public List<State> findAllByOrderByNameAsc();

    List<State> findByCountry_IdOrderByNameAsc(Integer countryId);
}
