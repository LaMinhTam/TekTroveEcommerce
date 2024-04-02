package com.tektrove.tektrovecustomer.setting;

import com.tektrovecommon.entity.setting.Setting;
import com.tektrovecommon.entity.setting.SettingCategory;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SettingRepository extends JpaRepository<Setting, Integer> {
    public List<Setting> findByCategory(SettingCategory category);

    public List<Setting> findByCategoryIn(SettingCategory... categories);
}
