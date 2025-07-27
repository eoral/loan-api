package com.eoral.loanapi.config;

import com.eoral.loanapi.util.Constants;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((authorize) -> authorize.anyRequest().authenticated())
                .httpBasic(Customizer.withDefaults())
                .csrf(c -> c.disable()); // Caution! Not recommended on production.
        return http.build();
    }

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().requestMatchers("/h2-console/**", "/swagger-ui/**", "/v3/api-docs*/**");
    }

    @Bean
    public UserDetailsService userDetailsService() {
        UserDetails admin = User.withUsername("admin")
                .password("{bcrypt}$2a$10$XbF4dX4bVvndDJf92fYu5O8psY91WRRjpMdwzQk1V0IA.uPws2H3.")
                .roles(Constants.ADMIN_ROLE)
                .build();
        UserDetails user = User.withUsername("user")
                .password("{bcrypt}$2a$10$BHuAUNk2VhkijoPY2MBnTOoD91lHdxY7S/1DufYucxg3HpZqa99AW")
                .roles(Constants.USER_ROLE)
                .build();
        return new InMemoryUserDetailsManager(admin, user);
    }
}
