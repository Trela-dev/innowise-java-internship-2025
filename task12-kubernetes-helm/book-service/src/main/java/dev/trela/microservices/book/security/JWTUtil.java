package dev.trela.microservices.book.security;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.trela.microservices.book.exception.JWTDecodingException;
import dev.trela.microservices.book.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;


import java.util.Base64;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class JWTUtil {

    private final MessageService messageService;
    private final ObjectMapper objectMapper = new ObjectMapper();


    private Map<String, Object> decodeJWTWithoutVerification(String jwtToken) {
        try {
            String[] parts = jwtToken.split("\\."); // Header, Payload, Signature
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]));
            return objectMapper.readValue(payloadJson, new TypeReference<>() {});
        } catch (Exception e) {
            throw new JWTDecodingException(messageService.getMessage("jwt.decoding.error"));
        }
    }

    protected List<String> extractRoles(String jwtToken) {
        Map<String, Object> claims = decodeJWTWithoutVerification(jwtToken);

        Object rolesObject = claims.get("roles");
        if(rolesObject instanceof String roleStr){
            return List.of(roleStr.split(","));
        }
        return List.of();
    }



}
