package com.tektrove.tektroveadmin.setting;

import com.tektrove.tektroveadmin.setting.currency.CurrencyService;
import com.tektrove.tektroveadmin.utils.FileUploadUtil;
import com.tektrovecommon.entity.setting.*;
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
import java.util.Optional;

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
        model.addAttribute("isAddRichText", true);
        settings.forEach(setting -> {
            model.addAttribute(setting.getKey(), setting.getValue());
        });
        return "settings/settings";
    }

    @PostMapping("/settings/save_general")
    public String saveGeneralSettings(MultipartFile fileImage, HttpServletRequest request, RedirectAttributes redirectAttributes) throws IOException {
        SettingBag generalSettingBag = settingService.getSettingBySettingCategories(SettingCategory.GENERAL, SettingCategory.CURRENCY);
        saveSiteLogo(fileImage, generalSettingBag);
        saveCurrencySymbol(request, generalSettingBag);
        updateSettingValuesFromForm(request, generalSettingBag);
        redirectAttributes.addFlashAttribute("message", "General settings saved");
        return "redirect:/settings#general";
    }

    @PostMapping("/settings/save_mail_server")
    public String saveMailServerSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        SettingBag generalSettingBag = settingService.getSettingBySettingCategories(SettingCategory.MAIL_SERVER);
        updateSettingValuesFromForm(request, generalSettingBag);
        redirectAttributes.addFlashAttribute("message", "Mail server settings saved");
        return "redirect:/settings#mailServer";
    }

    @PostMapping("/settings/save_mail_templates")
    public String saveMailTemplatesSettings(HttpServletRequest request, RedirectAttributes redirectAttributes) {
        SettingBag generalSettingBag = settingService.getSettingBySettingCategories(SettingCategory.MAIL_TEMPLATES);
        updateSettingValuesFromForm(request, generalSettingBag);
        redirectAttributes.addFlashAttribute("message", "Mail templates settings saved");
        return "redirect:/settings#mailTemplates";
    }

    private void saveCurrencySymbol(HttpServletRequest request, SettingBag generalSettingBag) {
        int currencyId = Integer.valueOf(request.getParameter("CURRENCY_ID"));
        Optional<Currency> findByIdResult = currencyService.findById(currencyId);

        if (findByIdResult.isPresent()) {
            Currency currency = findByIdResult.get();
            generalSettingBag.updateByKeyAndValue("CURRENCY_SYMBOL", currency.getSymbol());
        }
    }

    private static void saveSiteLogo(MultipartFile fileImage, SettingBag generalSettingBag) throws IOException {
        if (!fileImage.isEmpty()) {
            String fileName = StringUtils.cleanPath(fileImage.getOriginalFilename());
            String value = "/site-logo/" + fileName;
            generalSettingBag.updateByKeyAndValue("SITE_LOGO", value);
            String uploadDir = "../site-logo";
            FileUploadUtil.cleanDir(uploadDir);
            FileUploadUtil.saveFile(uploadDir, fileName, fileImage);
        }
    }

    public void updateSettingValuesFromForm(HttpServletRequest request, SettingBag settingBag) {
        settingBag.list().forEach(setting -> {
            String value = request.getParameter(setting.getKey());
            if (value != null) {
                setting.setValue(value);
            }
        });
        settingService.saveAll(settingBag.list());
    }
}
