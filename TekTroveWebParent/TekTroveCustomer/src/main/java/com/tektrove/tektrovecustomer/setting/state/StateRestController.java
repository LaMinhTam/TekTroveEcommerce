package com.tektrove.tektrovecustomer.setting.state;

import com.tektrovecommon.entity.setting.State;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class StateRestController {
    private final StateRepository stateRepository;

    public StateRestController(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    @GetMapping("/settings/list_by_country/{countryId}")
    public List<State> listByCountry(@PathVariable Integer countryId) {
        return stateRepository.findByCountry_IdOrderByNameAsc(countryId);
    }
}
