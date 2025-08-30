package dev.trela.config;

import dev.trela.repository.UserRepository;
import dev.trela.security.jwt.JWTAuthenticationProvider;
import dev.trela.security.jwt.JWTFilter;
import dev.trela.security.usernamepasswordauth.CustomUserDetailsService;
import dev.trela.security.usernamepasswordauth.JWTCustomUsernamePasswordAuthenticationFilter;
import dev.trela.service.MessageService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    UserRepository userRepository;
    private final JWTCustomUsernamePasswordAuthenticationFilter jwtCustomUsernamePasswordAuthenticationFilter;
    private final JWTFilter jwtFilter;
    private final JWTAuthenticationProvider jwtAuthenticationProvider;
;

    public SecurityConfig (UserRepository userRepository,
                           JWTCustomUsernamePasswordAuthenticationFilter jwtCustomUsernamePasswordAuthenticationFilter
            , @Lazy JWTFilter jwtFilter, JWTAuthenticationProvider jwtAuthenticationProvider){
        this.userRepository = userRepository;
        this.jwtCustomUsernamePasswordAuthenticationFilter = jwtCustomUsernamePasswordAuthenticationFilter;
        this.jwtFilter = jwtFilter;
        this.jwtAuthenticationProvider = jwtAuthenticationProvider;

    }

    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{

        http.csrf(csrf-> csrf.disable());

        http.authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/register/**").permitAll()
                .requestMatchers("/api/auth/login/**").permitAll()
                .anyRequest().authenticated()
//                .anyRequest().permitAll()
        );


        http.addFilterAt(jwtCustomUsernamePasswordAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        http.addFilterBefore(jwtFilter, JWTCustomUsernamePasswordAuthenticationFilter.class);

        return http.build();

    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }


    @Bean
    public AuthenticationManager authenticationManager(UserDetailsService userDetailsService, PasswordEncoder passwordEncoder){
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder);
        return new ProviderManager(List.of(authProvider,jwtAuthenticationProvider));
    }




}
