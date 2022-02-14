package com.example.user.entity;

import com.example.user.dto.RequestUser;
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

    @Column(nullable = false, unique = true)
    private Long memberId;

    public static User createUser(String email, String name, String encryptedPwd, Long memberId){
        User user = new User();
        user.email = email;
        user.name = name;
        user.encryptedPwd = encryptedPwd;
        user.memberId = memberId;
        return user;
    }

    public void updateUser(RequestUser user){
        this.email = user.getEmail();
        this.name = user.getName();
    }

}
