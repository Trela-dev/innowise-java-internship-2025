package dev.trela.config.security;

import dev.trela.security.jwt.JWTFilter;
import dev.trela.security.usernamepasswordauth.JWTCustomUsernamePasswordAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class FilterChainConfig {

    private final JWTCustomUsernamePasswordAuthenticationFilter jwtCustomUsernamePasswordAuthenticationFilter;
    private final JWTFilter jwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
                .csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register/**").permitAll()
                .requestMatchers("/api/auth/login/**").permitAll()
                .anyRequest().authenticated())
                .addFilterAt(jwtCustomUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
        .addFilterBefore(jwtFilter, JWTCustomUsernamePasswordAuthenticationFilter.class).build();
    }

}
