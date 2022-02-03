package com.example.user.entity;

import lombok.Data;
import lombok.Getter;

import javax.persistence.*;


@Entity
@Getter
@Table(name="users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    private Long id;

    @Column(nullable = false, length = 50, unique = true)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(nullable = false, unique = true)
    private String encryptedPwd;

    public static User createUser(String email, String name, String encryptedPwd){
        User user = new User();
        user.email = email;
        user.name = name;
        user.encryptedPwd = encryptedPwd;
        return user;
    }
}
