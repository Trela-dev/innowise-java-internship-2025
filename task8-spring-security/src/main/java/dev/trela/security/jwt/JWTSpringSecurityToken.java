package dev.trela.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

public class JWTSpringSecurityToken extends AbstractAuthenticationToken {
    private final String username;
    private final String token;

    public JWTSpringSecurityToken(String username, String token, Collection<? extends GrantedAuthority> authorities, boolean authenticated){
        super(authorities);
        this.username = username;
        this.token = token;
        setAuthenticated(authenticated);
    }


    @Override
    public Object getCredentials() {
        return token;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }
}
