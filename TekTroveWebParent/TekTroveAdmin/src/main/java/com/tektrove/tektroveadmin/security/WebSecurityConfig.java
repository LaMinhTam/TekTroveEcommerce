package com.tektrove.tektroveadmin.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class WebSecurityConfig {
    @Bean
    UserDetailsService userDetailsService() {
        return new TekTroveUserDetailsService();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(userDetailsService());
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    SecurityFilterChain configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.authenticationProvider(authenticationProvider());

        httpSecurity.authorizeHttpRequests(auth -> auth
                        .requestMatchers("/images/**", "/js/**", "/css/**", "/webjars/**").permitAll()
                        .requestMatchers("/users/**", "/settings/**").hasAuthority("Admin")
                        .requestMatchers("/categories/**", "/brands/**", "/articles/**", "/menus/**").hasAnyAuthority("Admin", "Editor")
                        .requestMatchers("/customers/**","/shipping/**","/reports/**").hasAnyAuthority("Admin","Salesperson")
                        .requestMatchers("/orders/**").hasAnyAuthority("Admin","Salesperson","Shipper")
                        .requestMatchers("/product/**").hasAnyAuthority("Admin","Salesperson","Editor","Shipper")
                        .anyRequest().authenticated())
                .formLogin(login -> login.loginPage("/login").usernameParameter("email")
                        .defaultSuccessUrl("/")
                        .permitAll())
                .logout(logout -> logout.logoutSuccessUrl("/login?logout").permitAll())
                .rememberMe(rememberMe -> rememberMe
                        .key("wc\"7u14UFcRXO%>")
                        .tokenValiditySeconds(86400)
                        .userDetailsService(this.userDetailsService()));

        return httpSecurity.build();
    }

//    @Bean
//    WebSecurityCustomizer configureWebSecurity() throws Exception {
//        return (web) -> web.ignoring().requestMatchers("/images/**", "/js/**", "/css/**", "/webjars/**");
//    }
}
