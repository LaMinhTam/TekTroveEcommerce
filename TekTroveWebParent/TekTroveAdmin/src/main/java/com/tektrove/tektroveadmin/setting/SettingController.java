package com.tektrove.tektroveadmin.setting;

import com.tektrove.tektroveadmin.utils.FileUploadUtil;
import com.tektrovecommon.entity.setting.Currency;
import com.tektrovecommon.entity.setting.GeneralSettingBag;
import com.tektrovecommon.entity.setting.Setting;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class SettingController {
    private final SettingService settingService;
    private final CurrencyService currencyService;

    public SettingController(SettingService settingService, CurrencyService currencyService) {
        this.settingService = settingService;
        this.currencyService = currencyService;
    }

    @GetMapping("/settings")
    public String listSettings(Model model) {
        List<Setting> settings = settingService.listAllSetting();
        List<Currency> currencies = currencyService.findAllOrderByNameAsc();
        model.addAttribute("currencies", currencies);
        settingService.getGeneralSetting();
        settings.forEach(setting -> {
            model.addAttribute(setting.getKey(), setting.getValue());
        });
        return "settings/settings";
    }

    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(MultipartFile fileImage, HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        GeneralSettingBag generalSettingBag = settingService.getGeneralSetting();
        saveSiteLogo(fileImage, generalSettingBag);
        saveGeneralSetting(request, generalSettingBag);
        return "redirect:/settings";
    }

    private static void saveSiteLogo(MultipartFile fileImage, GeneralSettingBag generalSettingBag) throws IOException {
        if (!fileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(fileImage.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            generalSettingBag.updateByKeyAndValue("SITE_LOGO", value);
            String uploadDir = "../site-logo";
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, fileImage);
        }
    }

    public void saveGeneralSetting(HttpServletRequest request, GeneralSettingBag settingBag) {
        settingBag.list().forEach(setting -> {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        });
        settingService.saveAll(settingBag.list());
    }
}
