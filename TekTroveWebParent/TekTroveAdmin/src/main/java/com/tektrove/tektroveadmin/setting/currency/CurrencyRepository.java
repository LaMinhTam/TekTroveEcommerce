package com.tektrove.tektroveadmin.setting.currency;

import com.tektrovecommon.entity.setting.Currency;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Integer> {
    List<Currency> findAllByOrderByName();
}
