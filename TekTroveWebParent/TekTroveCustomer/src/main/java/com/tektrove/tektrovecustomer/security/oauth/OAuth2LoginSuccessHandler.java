package com.tektrove.tektrovecustomer.security.oauth;

import com.tektrove.tektrovecustomer.customer.CustomerService;
import com.tektrovecommon.entity.AuthenticationType;
import com.tektrovecommon.entity.Customer;
import com.tektrovecommon.exception.CustomerNotFoundException;
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
public class OAuth2LoginSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
    @Autowired
    @Lazy
    private CustomerService customerService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws ServletException, IOException {
        CustomerOAuth2User oAuth2User = (CustomerOAuth2User) authentication.getPrincipal();
        String name = oAuth2User.getName();
        String email = oAuth2User.getEmail();
        String countryCode = request.getLocale().getCountry();
        String clientName = oAuth2User.getClientName();

        AuthenticationType authenticationType = getAuthenticationType(clientName);
        Customer customer = null;

        try {
            customer = customerService.getCustomerByEmail(email);
            oAuth2User.setFullName(customer.getFullName());
            customerService.updateAuthentication(customer, authenticationType);
        } catch (CustomerNotFoundException e) {
            customerService.addNewCustomerUponOAuthLogin(name, email, countryCode, authenticationType);
        }

        super.onAuthenticationSuccess(request, response, authentication);
    }

    private AuthenticationType getAuthenticationType(String clientName) {
        switch (clientName) {
            case "Google":
                return AuthenticationType.GOOGLE;
            case "Facebook":
                return AuthenticationType.FACEBOOK;
            default:
                return AuthenticationType.DATABASE;
        }
    }
}
