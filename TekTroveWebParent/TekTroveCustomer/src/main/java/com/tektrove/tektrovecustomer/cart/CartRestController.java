package com.tektrove.tektrovecustomer.cart;

import com.tektrove.tektrovecustomer.customer.CustomerService;
import com.tektrove.tektrovecustomer.utils.AuthenticationUtil;
import com.tektrovecommon.entity.customer.Customer;
import com.tektrovecommon.exception.CartItemException;
import com.tektrovecommon.exception.CustomerNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CartRestController {
    private final CartItemService cartItemService;
    private final CustomerService customerService;

    public CartRestController(CartItemService cartItemService, CustomerService customerService) {
        this.cartItemService = cartItemService;
        this.customerService = customerService;
    }

    @PostMapping("/cart/add/{productId}/{quantity}")
    public ResponseEntity<?> addProductToCart(HttpServletRequest request, @PathVariable Integer productId, @PathVariable Integer quantity) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            Integer updatedQuantity = cartItemService.addProduct(customer.getId(), productId, quantity);
            return ResponseEntity.ok().body(updatedQuantity + " item(s) of this product were added to your shopping cart.");
        } catch (CustomerNotFoundException | CartItemException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    private Customer getAuthenticatedCustomer(HttpServletRequest request) throws CustomerNotFoundException {
        String email = AuthenticationUtil.getEmailOfAuthenticatedCustomer(request);
        if (email == null) {
            throw new CustomerNotFoundException("You must login to continue.");
        }
        return customerService.getCustomerByEmail(email);
    }

    @PostMapping("/cart/update/{productId}/{quantity}")
    public ResponseEntity<?> updateProductToCart(HttpServletRequest request, @PathVariable Integer productId, @PathVariable Integer quantity) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            float updatedQuantity = cartItemService.updateQuantity(customer.getId(), productId, quantity);
            return ResponseEntity.ok().body(updatedQuantity);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/cart/remove/{productId}")
    public ResponseEntity<?> removeProductFromCart(HttpServletRequest request, @PathVariable Integer productId) {
        try {
            Customer customer = getAuthenticatedCustomer(request);
            cartItemService.removeProduct(customer.getId(), productId);
            return ResponseEntity.ok().body(productId);
        } catch (CustomerNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
