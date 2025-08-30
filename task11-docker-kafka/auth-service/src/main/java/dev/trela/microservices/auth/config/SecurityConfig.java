package dev.trela.microservices.auth.config;

import dev.trela.microservices.auth.repository.UserRepository;
import dev.trela.microservices.auth.security.JWTUtil;
import dev.trela.microservices.auth.security.usernamepasswordauth.CustomUserDetailsService;
import dev.trela.microservices.auth.security.usernamepasswordauth.JWTCustomUsernamePasswordAuthenticationFilter;
import dev.trela.microservices.auth.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final UserRepository userRepository;
    private final JWTUtil jwtUtil;
    private final MessageService messageService;
    private final StreamBridge streamBridge;

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http.csrf(CsrfConfigurer::disable)
        .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/register/**").permitAll()
                        .requestMatchers("/api/auth/login/**").permitAll()
                        .requestMatchers("/actuator/**").permitAll()
                        .anyRequest().authenticated())
                .addFilterAt(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class).build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }


    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(List.of(authProvider));
    }

    @Bean
    public JWTCustomUsernamePasswordAuthenticationFilter jwtAuthenticationFilter(){
        return new JWTCustomUsernamePasswordAuthenticationFilter(jwtUtil, messageService, streamBridge, authenticationManager(userDetailsService(), passwordEncoder()));
    }

}
