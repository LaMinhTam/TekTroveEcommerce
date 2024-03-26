package com.tektrove.tektroveadmin.setting.state;

import com.tektrovecommon.entity.setting.State;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StateService {
    private final StateRepository stateRepository;

    public StateService(StateRepository stateRepository) {
        this.stateRepository = stateRepository;
    }

    public State save(State state) {
        return stateRepository.save(state);
    }

    public void deleteById(Integer id) {
        stateRepository.deleteById(id);
    }

    public List<State> findByCountryOrderByNameAsc(Integer countryId) {
        return stateRepository.findByCountry_IdOrderByNameAsc(countryId);
    }
}
