package dev.trela.microservices.gateway.security.jwt;



import dev.trela.microservices.gateway.security.jwt.handler.JWTAuthenticationFailureHandler;
import dev.trela.microservices.gateway.security.jwt.handler.JWTAuthenticationSuccessHandler;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@RequiredArgsConstructor
@Slf4j
public class JWTFilter extends OncePerRequestFilter {


    private final AuthenticationManager authenticationManager;
    private final JWTAuthenticationSuccessHandler jwtAuthenticationSuccessHandler;
    private final JWTAuthenticationFailureHandler jwtAuthenticationFailureHandler;
    private static final int BEARER_PREFIX_LENGTH = 7;



    @Override
    protected void doFilterInternal(
           @NonNull HttpServletRequest request,
           @NonNull HttpServletResponse response,
           @NonNull FilterChain filterChain
    ) throws ServletException, IOException{

        String authorizationHeader = request.getHeader("Authorization");
        String path = request.getServletPath();




         if("/api/auth/login".equals(path) || "/api/auth/register".equals(path)|| path.startsWith("/actuator")
         ||"/fallbackRoute".equals(path) || path.startsWith("/error")) {
             filterChain.doFilter(request, response);
             return;
         }

         if(authorizationHeader != null && authorizationHeader.startsWith("Bearer ")){
             String token = authorizationHeader.substring(BEARER_PREFIX_LENGTH);
             JWTSpringSecurityToken jwtAuthenticationToken = new JWTSpringSecurityToken(null, token, null, false);
             try{
                 Authentication authenticationResult = authenticationManager.authenticate(jwtAuthenticationToken);
                 succesfulJWTAuthentication(request, response, filterChain, authenticationResult);
             }catch(AuthenticationException ex){
                unsuccessfulJwtAuthentication(request, response, filterChain, ex);
             }
         }else{
             filterChain.doFilter(request, response);
         }




    }



    private void succesfulJWTAuthentication(HttpServletRequest request,
                                            HttpServletResponse response,
                                            FilterChain filterChain,
                                            Authentication successAuthenticationToken) throws IOException, ServletException{


        SecurityContextHolder.getContext().setAuthentication(successAuthenticationToken);
        jwtAuthenticationSuccessHandler.onAuthenticationSuccess(request, response, successAuthenticationToken);
        filterChain.doFilter(request, response);
        log.info("Current context : {}", SecurityContextHolder.getContext().getAuthentication());
    }

    private void unsuccessfulJwtAuthentication(HttpServletRequest request, HttpServletResponse response,
                                               FilterChain filterChain, AuthenticationException exception)
            throws IOException {
        jwtAuthenticationFailureHandler.onAuthenticationFailure(request, response, exception);
    }


}
