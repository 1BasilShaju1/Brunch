package com.brunch.config;

import com.brunch.model.User;
import com.brunch.service.FileStorageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final FileStorageService fileStorageService;

    public SecurityConfig(FileStorageService fileStorageService) {
        this.fileStorageService = fileStorageService;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return username -> {
            User user = fileStorageService.findUserByEmail(username)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + username));

            return org.springframework.security.core.userdetails.User
                    .withUsername(user.getEmail())
                    .password(user.getPassword())
                    .roles(user.getRole())
                    .build();
        };
    }

    @Bean
    public AuthenticationSuccessHandler roleBasedAuthenticationSuccessHandler() {
        return (request, response, authentication) -> {
            boolean isRestaurant = authentication.getAuthorities().stream()
                    .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals("ROLE_RESTAURANT"));

            if (isRestaurant) {
                response.sendRedirect("/restaurant/dashboard");
            } else {
                response.sendRedirect("/customer/dashboard");
            }
        };
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                    "/",
                    "/register",
                    "/login",
                    "/menu/**",
                    "/restaurants/**",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                .requestMatchers(
                    "/customer/**",
                    "/cart/**",
                    "/checkout/**",
                    "/orders/**"
                ).hasRole("CUSTOMER")
                .requestMatchers(
                    "/restaurant/**"
                ).hasRole("RESTAURANT")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .usernameParameter("email")
                .passwordParameter("password")
                .successHandler(roleBasedAuthenticationSuccessHandler())
                .failureUrl("/login?error=true")
                .permitAll()
            )
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout=true")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll()
            )
            .exceptionHandling(ex -> ex
                .accessDeniedPage("/error")
            );

        return http.build();
    }
}
