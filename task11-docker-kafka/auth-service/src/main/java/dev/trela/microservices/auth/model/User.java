package dev.trela.microservices.auth.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @NotBlank(message = "{user.username.empty}")
    private String username;

    @NotBlank(message = "{user.password.notblank}")
    @Size(min = 8, message = "{user.password.too.short}")
    private String password;

    @NotBlank(message= "{email.is.blank}")
    @Email(message = "{email.not.valid}")
    private String email;


    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    private boolean accountNonExpired=true;
    private boolean accountNonLocked=true;
    private boolean credentialsNonExpired=true;
    private boolean enabled=true;

    public User(String username, String password, Role role, String email){
        this.username = username;
        this.password = password;
        this.role = role;
        this.email = email;
    }



}
