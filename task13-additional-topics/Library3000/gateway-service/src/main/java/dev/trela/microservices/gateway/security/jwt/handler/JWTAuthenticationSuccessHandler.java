package dev.trela.microservices.gateway.security.jwt.handler;



import dev.trela.microservices.gateway.service.MessageService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class JWTAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final MessageService messageService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication){
        log.info(messageService.getMessage("auth.jwt.success", authentication.getName()));
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
