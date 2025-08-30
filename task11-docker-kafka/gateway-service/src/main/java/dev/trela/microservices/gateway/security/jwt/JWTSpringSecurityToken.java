package dev.trela.microservices.gateway.security.jwt;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof JWTSpringSecurityToken that)) return false;
        if (!super.equals(o)) return false;
        return Objects.equals(username, that.username)
                &&
                Objects.equals(token, that.token)
                &&
                Objects.equals(getAuthorities(), that.getAuthorities());
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), username, token, getAuthorities());
    }


}
