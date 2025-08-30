package dev.trela.microservices.gateway.config.security;

import dev.trela.microservices.gateway.security.jwt.JWTAuthenticationProvider;
import dev.trela.microservices.gateway.security.jwt.JWTFilter;
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

    private final JWTFilter jwtFilter;


    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.csrf(csrf-> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register/**").permitAll()
                .requestMatchers("/api/auth/login/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .requestMatchers("/fallbackRoute").permitAll()
                .requestMatchers("/error").permitAll()
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();

    }



}
