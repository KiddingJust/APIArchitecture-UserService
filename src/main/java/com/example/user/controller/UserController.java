package com.example.user.controller;

import com.example.user.dto.RequestLogin;
import com.example.user.dto.RequestUser;
import com.example.user.dto.ResponseUser;
import com.example.user.entity.User;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/users")
    public ResponseUser createUser(@RequestBody @Valid RequestUser request){
        User user = userService.createUser(request);
        return ResponseUser.createResponse(user);
    }
}
