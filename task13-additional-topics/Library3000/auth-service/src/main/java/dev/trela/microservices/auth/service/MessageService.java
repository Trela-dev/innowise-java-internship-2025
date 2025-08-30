package dev.trela.microservices.auth.service;


import jakarta.servlet.http.HttpServletRequest;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.LocaleResolver;

import java.util.Locale;

@Service
@Getter
@Setter
@RequiredArgsConstructor
public class MessageService {

    private final MessageSource messageSource;
    private final LocaleResolver localeResolver;
    private final HttpServletRequest request;

    public String getMessage(String code) {
        Locale locale = localeResolver.resolveLocale(request);
        return messageSource.getMessage(code, null, locale);
    }

    public String getMessage(String code, Object... args) {
        Locale locale = localeResolver.resolveLocale(request);
        return messageSource.getMessage(code, args, locale);
    }

}
