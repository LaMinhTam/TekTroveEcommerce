package com.tektrove.tektroveadmin.currency;

import com.tektrove.tektroveadmin.setting.SettingRepository;
import com.tektrovecommon.entity.setting.Setting;
import com.tektrovecommon.entity.setting.SettingCategory;
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
@Rollback(false)
public class SettingRepositoryTests {
    @Autowired
    private SettingRepository settingRepository;

    @Test
    public void testCreateCurrencySetting() {
        List<Setting> settings = Arrays.asList(
                new Setting("SITE_NAME", "TekTrove", SettingCategory.GENERAL),
                new Setting("SITE_LOGO","logo.png", SettingCategory.GENERAL),
                new Setting("COPYRIGHT","Copyright (C) 2021 TekTrove Ltd.", SettingCategory.GENERAL)
        );
        settingRepository.saveAll(settings);
        List<Setting> settingIterator = settingRepository.findAll();
        assertThat(settings.size()).isEqualTo(settingIterator.size());
    }
}
