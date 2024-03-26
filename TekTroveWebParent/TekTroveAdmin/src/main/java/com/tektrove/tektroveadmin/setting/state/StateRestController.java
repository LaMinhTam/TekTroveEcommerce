package com.tektrove.tektroveadmin.setting.state;

import com.tektrovecommon.entity.setting.State;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/states")
public class StateRestController {
    private final StateService stateService;

    public StateRestController(StateService stateService) {
        this.stateService = stateService;
    }

    @GetMapping("/list_by_country/{countryId}")
    public List<State> listByCountry(@PathVariable Integer countryId) {
        return stateService.findByCountryOrderByNameAsc(countryId);
    }

    @PostMapping("/save")
    public State save(@RequestBody State state) {
        return stateService.save(state);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable Integer id) {
        stateService.deleteById(id);
    }
}
