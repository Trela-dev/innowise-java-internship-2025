package dev.trela.testing.service;


import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import java.util.Locale;

@Service
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

//null = optional argument
public String getMessage(String code){
    return messageSource.getMessage(code,null,currentLocale);
}

public String getMessage(String code,Object...args){
    return messageSource.getMessage(code,args,currentLocale);
}
}
