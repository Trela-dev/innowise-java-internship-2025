package dev.trela.microservices.auth.security.usernamepasswordauth;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.trela.microservices.auth.dto.UserLoginNotificationDTO;
import dev.trela.microservices.auth.model.User;
import dev.trela.microservices.auth.security.JWTUtil;
import dev.trela.microservices.auth.service.MessageService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;
import java.util.stream.Collectors;

@Slf4j
public class JWTCustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JWTUtil jwtUtil;
    private final MessageService messageService;
    private final StreamBridge streamBridge;

    public JWTCustomUsernamePasswordAuthenticationFilter(JWTUtil jwtUtil, MessageService messageService, StreamBridge streamBridge, AuthenticationManager authenticationManager) {
        this.jwtUtil = jwtUtil;
        this.messageService = messageService;
        this.streamBridge = streamBridge;
        this.setAuthenticationManager(authenticationManager);
        this.setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        ObjectMapper objectMapper = new ObjectMapper();
        User user;
        try {
            user = objectMapper.readValue(request.getInputStream(), User.class);
        } catch(IOException e){
            throw new AuthenticationServiceException(messageService.getMessage("auth.login.parse.error"));
        }

        String username = user.getUsername();
        String password = user.getPassword();


        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username, password);
        return this.getAuthenticationManager().authenticate(authRequest);

    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException {

        CustomUserDetails userDetails = (CustomUserDetails) authResult.getPrincipal();

        String username = userDetails.getUsername();
        String email = userDetails.getEmail();
        String roles = authResult.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.joining(","));

        sendLoginEvent(username, email);

        String jwtToken = jwtUtil.generateToken(username, roles);
        response.setHeader("Authorization", "Bearer " + jwtToken);
        response.setContentType("text/plain");
        response.getWriter().write(messageService.getMessage("auth.login.success.token"));
        response.getWriter().flush();

    }


    private void sendLoginEvent(String username, String email){
        UserLoginNotificationDTO userLoginNotificationDTO = new UserLoginNotificationDTO(username, email);
        log.info("Sending succesful login attempt event for user{} with email {}", username, email);
        boolean result = streamBridge.send("sendLoginEvent-out-0", userLoginNotificationDTO);
        log.info("Email event sent: {}", result);
    }



}
