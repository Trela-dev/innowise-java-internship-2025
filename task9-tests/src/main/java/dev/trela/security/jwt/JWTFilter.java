package dev.trela.security.jwt;

import dev.trela.security.jwt.handler.JWTAuthenticationFailureHandler;
import dev.trela.security.jwt.handler.JWTAuthenticationSuccessHandler;
import dev.trela.service.MessageService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.hibernate.validator.internal.util.stereotypes.Lazy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Log4j2
public class JWTFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final JWTAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    private final JWTAuthenticationFailureHandler jwtAuthenticationFailureHandler;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException{
        String authorizationHeader = request.getHeader("Authorization");
        String path = request.getServletPath();
         if("/api/auth/login".equals(path) || "/api/auth/register".equals(path)){
             filterChain.doFilter(request,response);
             return;
         }
         if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
             String token = authorizationHeader.substring(7);
             JWTSpringSecurityToken jwtAuthenticationToken = new JWTSpringSecurityToken(null,token,null,false);
             try{
                 Authentication authenticationResult = authenticationManager.authenticate(jwtAuthenticationToken);
                 succesfulJWTAuthentication(request,response,filterChain,authenticationResult);
             }catch(AuthenticationException ex){
                unsuccessfulJwtAuthentication(request,response,filterChain,ex);
             }
         }else{
             filterChain.doFilter(request,response);
         }
    }




    private void succesfulJWTAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain,
                                            Authentication successAuthenticationToken) throws IOException, ServletException{


        SecurityContextHolder.getContext().setAuthentication(successAuthenticationToken);
        jwtAuthenticationSuccessHandler.onAuthenticationSuccess(request,response,successAuthenticationToken);
        filterChain.doFilter(request,response);
        log.info("Current context : {}", SecurityContextHolder.getContext().getAuthentication());
    }

    private void unsuccessfulJwtAuthentication(HttpServletRequest request, HttpServletResponse response,
                                               FilterChain filterChain, AuthenticationException exception)
            throws IOException, ServletException {
        jwtAuthenticationFailureHandler.onAuthenticationFailure(request, response, exception);
    }


}
