package com.tektrove.tektroveadmin.setting.currency;

import com.tektrovecommon.entity.setting.Currency;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<Currency> findAllOrderByNameAsc() {
        return currencyRepository.findAllByOrderByName();
    }

    public Optional<Currency> findById(int id) {
        return currencyRepository.findById(id);
    }
}
