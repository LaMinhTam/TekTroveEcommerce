package com.tektrove.tektrovecustomer.security;

import com.tektrove.tektrovecustomer.customer.CustomerService;
import com.tektrovecommon.entity.AuthenticationType;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DatabaseLoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    @Lazy
    private CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomerUserDetails userDetails = (CustomerUserDetails) authentication.getPrincipal();
        userDetails.getCustomer();
        customerService.updateAuthentication(userDetails.getCustomer(), AuthenticationType.DATABASE);
        super.onAuthenticationSuccess(request, response, authentication);
    }
}
