package dev.trela.notification.functions;

import dev.trela.notification.dto.UserLoginNotificationDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.function.Consumer;

@Configuration
@Slf4j
public class MessageFunctions {

    @Bean
    public Consumer<UserLoginNotificationDTO> email(){
        return userLoginNotificationDTO -> {
            String username = userLoginNotificationDTO.username();
            String email = userLoginNotificationDTO.email();
            log.info("Sending succesful login attempt for user {} with email {}", username, email);

        };
    }

}
