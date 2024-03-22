package com.tektrove.tektroveadmin.setting;

import com.tektrovecommon.entity.setting.GeneralSettingBag;
import com.tektrovecommon.entity.setting.Setting;
import com.tektrovecommon.entity.setting.SettingCategory;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    public GeneralSettingBag getGeneralSetting() {
        List<Setting> settings = settingRepository.findByCategoryIn(List.of(SettingCategory.GENERAL, SettingCategory.CURRENCY));
        return new GeneralSettingBag(settings);
    }

    public void saveAll(Iterable<Setting> settings) {
        settingRepository.saveAll(settings);
    }


}
