package com.example.UserManagement.entity;

import com.example.UserManagement.Enum.Role;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@ToString
public class User {

    public User(String username, String email, String password, Role role){
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "username")
    private String username;
    @Column(name = "email")
    private String email;
    @Column(name = "password")
    private String password;
    @Column(name = "image")
    private String profilePicture;
    @Column(name = "is_blocked")
    private Boolean isBlocked;
    @Column(name = "role")
    @Enumerated(EnumType.STRING)
    private Role role;

}
