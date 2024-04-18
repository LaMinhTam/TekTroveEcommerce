package com.tektrove.tektroveadmin.order;

import com.tektrove.tektroveadmin.paging.PagingAndSortingHelper;
import com.tektrove.tektroveadmin.paging.PagingAndSortingParam;
import com.tektrove.tektroveadmin.setting.SettingService;
import com.tektrovecommon.entity.order.Order;
import com.tektrovecommon.entity.setting.*;
import com.tektrovecommon.exception.OrderNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;


@Controller
@RequestMapping("orders")
public class OrderController {
    private final OrderService orderService;
    private final SettingService settingService;
    private String defaultRedirectURL = "redirect:/orders/page/1?sortField=firstName&sortDir=asc";

    public OrderController(OrderService orderService, SettingService settingService) {
        this.orderService = orderService;
        this.settingService = settingService;
    }

    @GetMapping
    public String listAll() {
        return defaultRedirectURL;
    }

    @GetMapping("/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(moduleName = "orders") PagingAndSortingHelper helper,
            @PathVariable int pageNum,
            HttpServletRequest request) {
        orderService.listByPage(pageNum, helper);
        loadCurrencySetting(request);
        return "orders/orders";
    }

    private void loadCurrencySetting(HttpServletRequest request) {
        SettingBag currencySettings = settingService.getSettingBySettingCategories(SettingCategory.CURRENCY);

        for (Setting setting : currencySettings.list()) {
            request.setAttribute(setting.getKey(), setting.getValue());
        }
    }

    @GetMapping("/detail/{id}")
    public String viewDetail(@PathVariable int id, Model model, RedirectAttributes redirectAttributes, HttpServletRequest request) {
        try {
            Order order = orderService.findById(id);
            model.addAttribute("order", order);
            loadCurrencySetting(request);
            return "orders/order_detail_modal";
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/orders";
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable int id, RedirectAttributes redirectAttributes) {
        try {
            orderService.delete(id);
        redirectAttributes.addFlashAttribute("message", "The order ID " + id + " has been deleted successfully.");
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }

    @GetMapping("/edit/{id}")
    public String edit(@PathVariable int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Order order = orderService.findById(id);
            List<Country> countries = orderService.listCountries();
            model.addAttribute("order", order);
            model.addAttribute("pageTitle", "Edit Order (ID: " + id + ")");
            return "orders/order_form";
        } catch (OrderNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return "redirect:/orders";
        }
    }
}
