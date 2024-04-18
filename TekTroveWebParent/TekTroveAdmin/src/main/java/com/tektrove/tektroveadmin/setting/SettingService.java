package com.tektrove.tektroveadmin.setting;

import com.tektrovecommon.entity.setting.Setting;
import com.tektrovecommon.entity.setting.SettingBag;
import com.tektrovecommon.entity.setting.SettingCategory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SettingService {
    private final SettingRepository settingRepository;

    public SettingService(SettingRepository settingRepository) {
        this.settingRepository = settingRepository;
    }

    public List<Setting> listAllSetting() {
        return settingRepository.findAll();
    }

    public SettingBag getSettingBySettingCategories(SettingCategory... settingCategories) {
        List<Setting> settings = settingRepository.findByCategoryIn(settingCategories);
        return new SettingBag(settings);
    }

    public void saveAll(Iterable<Setting> settings) {
        settingRepository.saveAll(settings);
    }


}
