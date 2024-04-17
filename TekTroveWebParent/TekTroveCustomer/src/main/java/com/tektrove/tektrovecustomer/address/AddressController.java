package com.tektrove.tektrovecustomer.address;

import com.tektrove.tektrovecustomer.customer.CustomerService;
import com.tektrove.tektrovecustomer.utils.AuthenticationUtil;
import com.tektrovecommon.entity.customer.Address;
import com.tektrovecommon.entity.customer.Customer;
import com.tektrovecommon.entity.setting.Country;
import com.tektrovecommon.exception.CustomerNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/addresses")
public class AddressController {
    private final AddressService addressService;
    private final CustomerService customerService;

    public AddressController(AddressService addressService, CustomerService customerService) {
        this.addressService = addressService;
        this.customerService = customerService;
    }

    @GetMapping
    public String getAddresses(Model model, HttpServletRequest request) throws CustomerNotFoundException {
        Customer customer = getAuthenticatedCustomer(request);
        List<Address> addresses = addressService.listAll(customer.getId());
        boolean usePrimaryAsDefault = true;
        for (Address address : addresses) {
            if (address.isDefaultAddress()) {
                usePrimaryAsDefault = false;
                break;
            }
        }
        model.addAttribute("addresses", addresses);
        model.addAttribute("customer", customer);
        model.addAttribute("usePrimaryAsDefault", usePrimaryAsDefault);
        return "addresses/addresses";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = AuthenticationUtil.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("You must login to add products to cart.");
        }
        return customerService.getCustomerByEmail(email);
    }

    @GetMapping("/new")
    public String newAddress(Model model, HttpServletRequest request) throws CustomerNotFoundException {
        List<Country> countries = customerService.getCountries();
        model.addAttribute("countries", countries);
        model.addAttribute("pageTitle", "Add New Address");
        model.addAttribute("address", new Address());
        return "addresses/address_form";
    }

    @GetMapping("/edit/{id}")
    public String editAddress(Model model, HttpServletRequest request, @PathVariable Integer id) throws CustomerNotFoundException {
        Customer customer = getAuthenticatedCustomer(request);
        Address address = addressService.get(customer.getId(), id);
        List<Country> countries = customerService.getCountries();

        model.addAttribute("countries", countries);
        model.addAttribute("pageTitle", "Edit Address (ID: " + id + ")");
        model.addAttribute("address", address);
        return "addresses/address_form";
    }

    @PostMapping("/save")
    public String saveAddress(Address address, RedirectAttributes redirectAttributes, HttpServletRequest request) throws CustomerNotFoundException {
        Customer customer = getAuthenticatedCustomer(request);
        addressService.save(address, customer);
        redirectAttributes.addFlashAttribute("message", "The address has been saved successfully.");
        return "redirect:/addresses";
    }

    @GetMapping("/delete/{id}")
    public String deleteAddress(RedirectAttributes redirectAttributes, HttpServletRequest request, @PathVariable Integer id) throws CustomerNotFoundException {
        Customer customer = getAuthenticatedCustomer(request);
        addressService.delete(customer.getId(), id);
        redirectAttributes.addFlashAttribute("message", "The address ID " + id + " has been deleted successfully.");
        return "redirect:/addresses";
    }

    @GetMapping("/set_default/{id}")
    public String setDefaultAddress(RedirectAttributes redirectAttributes, HttpServletRequest request, @PathVariable Integer id) throws CustomerNotFoundException {
        Customer customer = getAuthenticatedCustomer(request);
        addressService.setDefaultAddress(customer.getId(), id);
        redirectAttributes.addFlashAttribute("message", "The address ID " + id + " has been set as default successfully.");
        return "redirect:/addresses";
    }
}
