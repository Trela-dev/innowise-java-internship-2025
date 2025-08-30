package dev.trela.microservices.book.service;


import lombok.Getter;
import lombok.Setter;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
@Getter
@Setter
public class MessageService {
private final MessageSource messageSource;
private Locale currentLocale;

public MessageService(MessageSource messageSource){
    this.messageSource = messageSource;
    this.currentLocale = Locale.ENGLISH;
}
public void setLocale(Locale locale){
    this.currentLocale = locale;
}

public String getMessage(String code){
    return messageSource.getMessage(code,null,currentLocale);
}

public String getMessage(String code,Object...args){
    return messageSource.getMessage(code,args,currentLocale);
}

public boolean isCurrentLocale(String language) {
        return this.currentLocale.getLanguage().equals(language);
}

}
