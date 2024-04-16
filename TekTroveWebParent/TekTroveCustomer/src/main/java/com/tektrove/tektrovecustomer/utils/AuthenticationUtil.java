package com.tektrove.tektrovecustomer.utils;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;

public class AuthenticationUtil {

    public static String getEmailOfAuthenticatedCustomer(HttpServletRequest request) {
        Object principal = request.getUserPrincipal();
        if(principal == null){
            return null;
        }
        String customerEmail = null;
        if (principal instanceof UsernamePasswordAuthenticationToken || principal instanceof RememberMeAuthenticationToken) {
            customerEmail = request.getUserPrincipal().getName();
        } else if (principal instanceof OAuth2AuthenticationToken oauth2Token) {
            customerEmail = oauth2Token.getPrincipal().getAttribute("email");
        }
        return customerEmail;
    }
}
