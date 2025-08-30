package dev.trela.microservices.auth.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JWTUtil {
    @Value("${jwt.secret}")
    private String secretKey;
    @Value("${jwt.expiration.time-minutes}")
    private long expirationTimeMinutes;
    private static final int SECONDS_IN_MINUTE = 60;
    private static final int MILLISECONDS_IN_SECOND = 1000;

    private Key getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, String roles){
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + getExpirationTimeInMillis()))
                .signWith(getSigningKey())
                .compact();
    }

    private long getExpirationTimeInMillis() {
        return expirationTimeMinutes  * SECONDS_IN_MINUTE * MILLISECONDS_IN_SECOND;
    }

}
