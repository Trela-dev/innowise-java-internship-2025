package dev.trela.microservices.auth.service;



import dev.trela.microservices.auth.exception.DefaultRoleNotFoundException;
import dev.trela.microservices.auth.exception.UsernameAlreadyInUseException;
import dev.trela.microservices.auth.model.Role;
import dev.trela.microservices.auth.model.User;
import dev.trela.microservices.auth.repository.RoleRepository;
import dev.trela.microservices.auth.repository.UserRepository;
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


    public void registerUser(String username, String password, String email){

        String encryptedPassword = passwordEncoder.encode(password);

        Role userRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(()-> new DefaultRoleNotFoundException(messageService.getMessage("user.role.default.missing")));

        userRepository.findByUsername(username)
                .ifPresent(user -> {
                    throw new UsernameAlreadyInUseException(messageService.getMessage("user.username.duplicate"));
                });

        userRepository.findByEmail(email)
                .ifPresent(user -> {
                    throw new UsernameAlreadyInUseException(messageService.getMessage("user.email.duplicate"));
                });


        User newUser = new User(username, encryptedPassword, userRole, email);

        userRepository.save(newUser);



    }






}
