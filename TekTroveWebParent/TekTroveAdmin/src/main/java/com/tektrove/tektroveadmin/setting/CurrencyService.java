package com.tektrove.tektroveadmin.setting;

import com.tektrovecommon.entity.setting.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CurrencyService {
    private final CurrencyRepository currencyRepository;

    public CurrencyService(CurrencyRepository currencyRepository) {
        this.currencyRepository = currencyRepository;
    }

    public List<Currency> findAllOrderByNameAsc() {
        return currencyRepository.findAllByOrderByName();
    }
}
