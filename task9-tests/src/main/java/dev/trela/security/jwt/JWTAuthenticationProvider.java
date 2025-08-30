package dev.trela.security.jwt;

import dev.trela.security.JWTUtil;
import dev.trela.service.MessageService;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JWTAuthenticationProvider implements AuthenticationProvider {

    private final JWTUtil jwtUtil;
    private final MessageService messageService;
    public JWTAuthenticationProvider(JWTUtil jwtUtil, MessageService messageService){
        this.jwtUtil = jwtUtil;
        this.messageService = messageService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException{
        String token = (String) authentication.getCredentials();
        if(jwtUtil.validateToken(token)){
            String username = jwtUtil.extractUsername(token);
            String roles = jwtUtil.extractRoles(token);
            Collection<? extends GrantedAuthority> authorities = convertStringRolesToAuthorities(roles);
            return new JWTSpringSecurityToken(username,token,authorities,true);
        }
        throw new AuthenticationException(messageService.getMessage("auth.jwt.invalid")) {};
    }

    private Collection<? extends GrantedAuthority> convertStringRolesToAuthorities(String rolesString) {
        if (rolesString == null || rolesString.isEmpty()) {
            return List.of();
        }
        return Arrays.stream(rolesString.split(","))
                .map(role -> new SimpleGrantedAuthority(role.trim()))
                .collect(Collectors.toList());
    }


    public boolean supports(Class<?> authentication){
        return JWTSpringSecurityToken.class.isAssignableFrom(authentication);
    }



}
