package com.tektrove.tektroveadmin.shippingRate;

import com.tektrove.tektroveadmin.paging.PagingAndSortingHelper;
import com.tektrove.tektroveadmin.paging.PagingAndSortingParam;
import com.tektrovecommon.entity.ShippingRate;
import com.tektrovecommon.entity.setting.Country;
import com.tektrovecommon.exception.ShippingRateException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/shipping_rates")
public class ShippingRateController {
    private final ShippingRateService shippingRateService;
    private String defaultRedirectURL = "redirect:/shipping_rates/page/1?sortField=id&sortDir=asc";

    public ShippingRateController(ShippingRateService shippingRateService) {
        this.shippingRateService = shippingRateService;
    }

    @GetMapping
    public String listAll() {
        return defaultRedirectURL;
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(moduleName = "shipping_rates") PagingAndSortingHelper helper,
            @PathVariable("pageNum") int pageNum,
            Model model) {
        shippingRateService.listByPage(pageNum, helper);
        model.addAttribute("helper", helper);
        return "shipping_rates/shipping_rates";
    }

    @PostMapping("/save")
    public String saveShippingRate(ShippingRate shippingRate, RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.save(shippingRate);
            redirectAttributes.addFlashAttribute("message", "The shipping rate has been saved successfully");
        } catch (ShippingRateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/new")
    public String newShippingRate(Model model) {
        List<Country> countries = shippingRateService.listAllCountries();
        ShippingRate shippingRate = new ShippingRate();
        model.addAttribute("shipping_rate", shippingRate);
        model.addAttribute("countries", countries);
        model.addAttribute("pageTitle", "Create New Shipping Rate");
        return "shipping_rates/shipping_rates_form";
    }

    @GetMapping("/edit/{id}")
    public String editShippingRate(@PathVariable("id") Integer id, Model model, RedirectAttributes redirectAttributes) {
        try {
            ShippingRate shippingRate = shippingRateService.get(id);
            List<Country> countries = shippingRateService.listAllCountries();
            model.addAttribute("shipping_rate", shippingRate);
            model.addAttribute("countries", countries);
            model.addAttribute("pageTitle", "Edit Shipping Rate (ID: " + id + ")");
            return "shipping_rates/shipping_rates_form";
        } catch (ShippingRateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/delete/{id}")
    public String deleteShippingRate(@PathVariable("id") Integer id, RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The shipping rate ID " + id + " has been deleted successfully");
        } catch (ShippingRateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/{id}/enabled/{codSupported}")
    public String updateCODSupport(@PathVariable("id") Integer id, @PathVariable("codSupported") boolean codSupported, RedirectAttributes redirectAttributes) {
        try {
            shippingRateService.updateCODSupport(id, codSupported);
            redirectAttributes.addFlashAttribute("message", "COD support for shipping rate ID " + id + " has been updated successfully");
        } catch (ShippingRateException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }
}
