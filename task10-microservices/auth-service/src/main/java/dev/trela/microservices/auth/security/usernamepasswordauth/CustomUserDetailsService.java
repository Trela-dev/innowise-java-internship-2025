package dev.trela.microservices.auth.security.usernamepasswordauth;


import dev.trela.microservices.auth.model.User;
import dev.trela.microservices.auth.repository.UserRepository;
import dev.trela.microservices.auth.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private MessageService messageService;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }
    @Autowired
    public void setMessageService(MessageService messageService){
        this.messageService = messageService;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(messageService.getMessage("auth.user.notfound", username)));
        return new CustomUserDetails(user);
    }
}
