package com.example.demo2026.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import com.example.demo2026.entity.User;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // Cho phép sử dụng @PreAuthorize
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/register", "/css/**", "/js/**", "/error", "/uploads/**").permitAll()
                .requestMatchers("/stats/**", "/orders/all", "/products/save", "/products/edit/**", "/products/delete/**").hasRole("ADMIN")
                .requestMatchers("/promo/save", "/promo/edit/**", "/promo/delete/**").hasRole("ADMIN")
                .requestMatchers("/promo").hasAnyRole("ADMIN", "CUSTOMER")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .successHandler(successHandler())
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex.accessDeniedPage("/home")); // Nếu bị 403 thì về home

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return NoOpPasswordEncoder.getInstance();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            User user = (User) authentication.getPrincipal();
            System.out.println(">>> [LOGIN SUCCESS] User: " + user.getUsername() + ", Role: " + user.getRole());
            // Không cần lưu vào session - tất cả controller dùng SecurityContextHolder
            response.sendRedirect("/home");
        };
    }
}