package com.tektrove.tektrovecustomer.security;

import com.tektrove.tektrovecustomer.security.oauth.CustomerOAuth2UserService;
import com.tektrove.tektrovecustomer.security.oauth.OAuth2LoginSuccessHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Autowired
    private CustomerOAuth2UserService customerOAuth2UserService;
    @Autowired
    private OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    @Autowired
    private DatabaseLoginSuccessHandler databaseLoginSuccessHandler;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomerUserDetailsService();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService());
        provider.setPasswordEncoder(passwordEncoder());
        return provider;
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/account_details","/update_account_details","/cart").authenticated()
                        .anyRequest().permitAll())
                .formLogin(login -> login.loginPage("/login").usernameParameter("email")
                        .successHandler(databaseLoginSuccessHandler)
                        .defaultSuccessUrl("/")
                        .permitAll())
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/login")
                        .userInfoEndpoint(userInfo -> userInfo.userService(customerOAuth2UserService))
                        .successHandler(oAuth2LoginSuccessHandler))
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                .rememberMe(rememberMe -> rememberMe
                        .key("wc\"7u14UFcRXO%>")
                        .tokenValiditySeconds(86400)
                        .userDetailsService(this.userDetailsService()))
                .sessionManagement(sessionManagement -> sessionManagement
                        .sessionCreationPolicy(SessionCreationPolicy.ALWAYS));

        return httpSecurity.build();
    }
}
