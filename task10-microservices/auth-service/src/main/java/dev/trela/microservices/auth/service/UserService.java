package dev.trela.microservices.auth.service;



import dev.trela.microservices.auth.exception.DefaultRoleNotFoundException;
import dev.trela.microservices.auth.exception.UsernameAlreadyInUseException;
import dev.trela.microservices.auth.model.Role;
import dev.trela.microservices.auth.model.User;
import dev.trela.microservices.auth.repository.RoleRepository;
import dev.trela.microservices.auth.repository.UserRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final MessageService messageService;

    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       PasswordEncoder passwordEncoder,
                       MessageService messageService){
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
        this.messageService = messageService;
    }


    public void registerUser(String username,String password){


        if(username==null || username.trim().isEmpty()){
            throw new IllegalArgumentException(messageService.getMessage("user.username.empty"));
        }
        if(password == null || password.length()<8){
            throw new IllegalArgumentException(messageService.getMessage("user.password.too.short"));
        }

        String encryptedPassword = passwordEncoder.encode(password);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(()-> new DefaultRoleNotFoundException(messageService.getMessage("user.role.default.missing")));

        User newUser = new User(username,encryptedPassword,userRole);

        try {
            userRepository.save(newUser);
        } catch (DataIntegrityViolationException e) {
            throw new UsernameAlreadyInUseException(messageService.getMessage("user.username.duplicate"));

        }

    }



}
