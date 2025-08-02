package org.shoppingdashboard.config;

import org.shoppingdashboard.Filter.JwtFilter;
import org.shoppingdashboard.Service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtFilter jwtFilter) throws Exception {
        http.csrf().disable() // Disable CSRF for stateless authentication
            .authorizeHttpRequests()
            .antMatchers("/customer/**", "/admin/**", "/product", "/", "/login", "/register","/profile/**","/images/**","/api/**","/logo/**").permitAll()
            .anyRequest().authenticated()
            .and()
            .formLogin()
            .loginPage("/")
            .defaultSuccessUrl("/customer/product", true)
            .permitAll()
            .and()
            .logout()
            .logoutSuccessUrl("/login")
            .permitAll();

        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }
}