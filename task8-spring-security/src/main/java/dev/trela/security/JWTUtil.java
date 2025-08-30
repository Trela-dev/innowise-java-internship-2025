package dev.trela.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;

import java.security.Key;
import java.util.Date;

import io.jsonwebtoken.io.Decoders;
import org.springframework.stereotype.Component;

@Component
public class JWTUtil {
    @Value("${jwt.secret}")
    private String secretKey;

    private final long expirationTime = 60 * 60 * 1000;

    private Key getSigningKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateToken(String username, String roles){
        return Jwts.builder()
                .subject(username)
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expirationTime))
                .signWith(getSigningKey())
                .compact();
    }


    private Claims getClaims(String token){
        return Jwts.parser()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getPayload();
    }

    public boolean validateToken(String token){
        try{
            Claims claims = getClaims(token);
            return !claims.getExpiration().before(new Date());
        }catch(Exception e){
            return false;
        }
    }

    public String extractUsername(String token) {
        return getClaims(token).getSubject();
    }

    public String extractRoles(String token) {
        return getClaims(token).get("roles", String.class);

    }





}
