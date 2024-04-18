package com.tektrove.tektrovecustomer.setting;

import com.tektrovecommon.entity.setting.Setting;
import com.tektrovecommon.entity.setting.SettingCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {
    private final SettingRepository settingRepository;

    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public List<Setting> getSettings(SettingCategory... categories) {
        return settingRepository.findByCategoryIn(categories);
    }
}
