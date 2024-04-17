package com.tektrove.tektrovecustomer.cart;

import com.tektrove.tektrovecustomer.customer.CustomerService;
import com.tektrove.tektrovecustomer.utils.AuthenticationUtil;
import com.tektrovecommon.entity.customer.CartItem;
import com.tektrovecommon.entity.customer.Customer;
import com.tektrovecommon.exception.CustomerNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class CartController {
    private final CartItemService cartItemService;
    private final CustomerService customerService;

    public CartController(CartItemService cartItemService, CustomerService customerService) {
        this.cartItemService = cartItemService;
        this.customerService = customerService;
    }

    @GetMapping("/cart")
    public String viewCart(Model model, HttpServletRequest request) throws CustomerNotFoundException {
        Customer customer = getAuthenticatedCustomer(request);
        List<CartItem> cartItems = cartItemService.listCartItems(customer);
        double estimatedTotal = cartItems.stream()
                .mapToDouble(CartItem::getSubTotal)
                .sum();

        model.addAttribute("cartItems", cartItems);
        model.addAttribute("estimatedTotal", estimatedTotal);
        return "cart/shopping_cart";
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = AuthenticationUtil.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("You must login to add products to cart.");
        }
        return customerService.getCustomerByEmail(email);
    }
}
