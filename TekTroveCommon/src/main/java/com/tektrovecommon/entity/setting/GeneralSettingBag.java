package com.tektrovecommon.entity.setting;

import java.util.List;

public class GeneralSettingBag extends SettingBag {
    public GeneralSettingBag(List<Setting> settingList) {
        super(settingList);
    }

    public void updateByKeyAndValue(String key, String value) {
        super.update(key, value);
    }

    public void updateCurrencySymbol(String value) {
        super.update("CURRENCY_SYMBOL", value);
    }

    public void updateCurrencyCode(String value) {
        super.update("SITE_LOGO", value);
    }
}
