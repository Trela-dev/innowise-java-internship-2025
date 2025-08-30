package dev.trela.security.usernamepasswordauth;


import com.fasterxml.jackson.databind.ObjectMapper;
import dev.trela.model.User;
import dev.trela.security.JWTUtil;
import dev.trela.service.MessageService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.stream.Collectors;

@Component
public class JWTCustomUsernamePasswordAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final JWTUtil jwtUtil;
    private final MessageService messageService;

    public JWTCustomUsernamePasswordAuthenticationFilter(@Lazy AuthenticationManager authenticationManager
    , JWTUtil jwtUtil,MessageService messageService){
        this.jwtUtil = jwtUtil;
        this.setAuthenticationManager(authenticationManager);
        this.messageService = messageService;
        this.setFilterProcessesUrl("/api/auth/login");
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        ObjectMapper objectMapper = new ObjectMapper();
        User user;
        try {
            user = objectMapper.readValue(request.getInputStream(), User.class);
        }
        catch(IOException e){
            throw new AuthenticationServiceException(messageService.getMessage("auth.login.parse.error"));
        }

        String username = user.getUsername();
        String password = user.getPassword();



        UsernamePasswordAuthenticationToken authRequest = UsernamePasswordAuthenticationToken.unauthenticated(username,password);
        return this.getAuthenticationManager().authenticate(authRequest);

    }
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
                                            Authentication authResult) throws IOException, ServletException {

        String username = authResult.getName();
        String roles = authResult.getAuthorities().stream()
                .map(grantedAuthority -> grantedAuthority.getAuthority())
                .collect(Collectors.joining(","));

        String jwtToken = jwtUtil.generateToken(username, roles);
        response.setHeader("Authorization", "Bearer " + jwtToken);
        response.setContentType("text/plain");
        response.getWriter().write(messageService.getMessage("auth.login.success.token"));
        response.getWriter().flush();



    }

//    @Override
//    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response,
//                                              AuthenticationException failed) throws IOException, ServletException{
//
//        super.unsuccessfulAuthentication(request,response,failed);
//        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
//        response.getWriter().write("Invalid credentials");
//
//    }



}
