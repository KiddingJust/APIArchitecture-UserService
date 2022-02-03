package com.example.user.dto;

import com.example.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
public class ResponseUser {

    private String email;
    private String name;
    private String pwd;

    public static ResponseUser createResponse(User user){
        ResponseUser response = new ResponseUser();
        response.email = user.getEmail();
        response.name = user.getName();
        response.pwd = user.getEncryptedPwd();
        return response;
    }
}
