package com.tektrove.tektroveadmin.customer;

import com.tektrove.tektroveadmin.paging.PagingAndSortingHelper;
import com.tektrove.tektroveadmin.paging.PagingAndSortingParam;
import com.tektrovecommon.entity.customer.Customer;
import com.tektrovecommon.entity.setting.Country;
import com.tektrovecommon.exception.CustomerNotFoundException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("customers")
public class CustomerController {
    private final CustomerService customerService;
    private final String defaultRedirectURL = "redirect:/customers/page/1?sortField=firstName&sortDir=asc";

    public CustomerController(CustomerService customerService) {
        this.customerService = customerService;
    }

    @GetMapping
    public String listAll() {
        return defaultRedirectURL;
    }


    @GetMapping("/page/{pageNum}")
    public String listByPage(
            @PagingAndSortingParam(moduleName = "customers") PagingAndSortingHelper helper,
            @PathVariable int pageNum) {
        customerService.listByPage(pageNum, helper);
        return "customers/customers";
    }

    @GetMapping("/edit/{id}")
    public String editCustomer(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.get(id);
            List<Country> countries = customerService.listAllCountry();
            model.addAttribute("customer", customer);
            model.addAttribute("countries", countries);
            model.addAttribute("pageTitle", "Edit Customer (ID: " + id + ")");
            return "customers/customer_form";
        } catch (CustomerNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @GetMapping("/{id}/enabled/{status}")
    public String updateCustomerEnabledStatus(@PathVariable("id") Integer id, @PathVariable("status") boolean enabled) {
        customerService.updateCustomerEnabledStatus(id, enabled);
        return defaultRedirectURL;
    }

    @GetMapping("/detail/{id}")
    public String viewCustomerDetails(@PathVariable("id") int id, Model model, RedirectAttributes redirectAttributes) {
        try {
            Customer customer = customerService.get(id);
            model.addAttribute("customer", customer);
            return "customers/customer_detail_modal";
        } catch (CustomerNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return defaultRedirectURL;
        }
    }

    @PostMapping("/save")
    public String saveCustomer(Customer customer, RedirectAttributes redirectAttributes) {
        customerService.save(customer);
        redirectAttributes.addFlashAttribute("message", "The customer has been updated successfully.");
        return defaultRedirectURL;
    }

    @GetMapping("/delete/{id}")
    public String deleteCustomer(@PathVariable("id") int id, RedirectAttributes redirectAttributes) {
        try {
            customerService.delete(id);
            redirectAttributes.addFlashAttribute("message", "The customer ID " + id + " has been deleted successfully.");
        } catch (CustomerNotFoundException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
        }
        return defaultRedirectURL;
    }
}
