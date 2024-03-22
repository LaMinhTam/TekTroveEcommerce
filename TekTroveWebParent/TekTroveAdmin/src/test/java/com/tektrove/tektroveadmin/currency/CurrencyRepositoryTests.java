package com.tektrove.tektroveadmin.currency;

import com.tektrove.tektroveadmin.setting.CurrencyRepository;
import com.tektrovecommon.entity.setting.Currency;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.Rollback;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Rollback(value = false)
public class CurrencyRepositoryTests {
    @Autowired
    private CurrencyRepository currencyRepository;

    @Test
    public void testCreateCurrencies() {
        List<Currency> currencies = Arrays.asList(
                new Currency("United States Dollar", "$", "USD"),
                new Currency("Euro", "€", "EUR"),
                new Currency("British Pound", "£", "GBP"),
                new Currency("Indian Rupee", "₹", "INR"),
                new Currency("Swiss Franc", "CHF", "CHF"),
                new Currency("Malaysian Ringgit", "RM", "MYR"),
                new Currency("Japanese Yen", "¥", "JPY"),
                new Currency("Chinese Yuan Renminbi", "¥", "CNY"),
                new Currency("Swedish Krona", "kr", "SEK"),
                new Currency("New Zealand Dollar", "NZ$", "NZD"),
                new Currency("Hong Kong Dollar", "HK$", "HKD"),
                new Currency("Norwegian Krone", "kr", "NOK"),
                new Currency("Vietnamese Dong", "₫", "VND")
        );
        currencyRepository.saveAll(currencies);
        List<Currency> currencyIterator = currencyRepository.findAll();
        assertThat(currencyIterator).size().isEqualTo(currencies.size());
    }
}
