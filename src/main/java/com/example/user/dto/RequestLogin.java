package com.example.user.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class RequestLogin {
    @NotNull
    @Email
    private String email;

    @NotNull
    private String pwd;
}
