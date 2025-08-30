package dev.trela.controller;

import dev.trela.model.User;
import dev.trela.service.MessageService;
import dev.trela.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;
    private final MessageService messageService;

    public AuthController(UserService userService,MessageService messageService){
        this.userService = userService;
        this.messageService = messageService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody @Valid User user){
       userService.registerUser(user.getUsername(),user.getPassword());
       return ResponseEntity.ok(messageService.getMessage("auth.register.success"));
    }

    @PostMapping("/login")
    public void login(@RequestBody @Valid User user) {
        // This method is empty because authentication is handled by a JWTCustomUsernamePasswordAuthenticationFilter
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/test")
    public String testEndpoint(){
        return "Access Granted";
    }




}
