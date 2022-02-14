package com.example.user.controller;

import com.example.user.dto.RequestLogin;
import com.example.user.dto.RequestUser;
import com.example.user.dto.Response;
import com.example.user.dto.ResponseUser;
import com.example.user.entity.User;
import com.example.user.messagequeue.KafkaProducer;
import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Slf4j
public class UserController {

    private final UserService userService;
    private final KafkaProducer kafkaProducer;
    private final Environment env;

    @Value("${greetings}")
    private String hello;

    //강기1 공누8로 id설정함
    @PostMapping("/users")
    public ResponseUser createUser(@RequestBody @Valid RequestUser request){
        User user = userService.createUser(request);
        return ResponseUser.createResponse(user);
    }

    @PostMapping("/users/{id}")
    public ResponseUser updateUser(@RequestBody @Valid RequestUser request){
        ResponseUser response = kafkaProducer.sendUpdateUser("ecommerce-member-topic", request);
        return response;
    }

//    @GetMapping("/users")
//    public List<ResponseUser> getUserAll(){
//        List<User> user = userService.getAllUser();
//        List<ResponseUser> responseUserList = user.stream().map(
//                                                u -> ResponseUser.createResponse(u))
//                                                .collect(Collectors.toList());
//        return responseUserList;
//    }
    @GetMapping("/users")
    public Response getUserAll(){
        Response user = userService.getAllUser();
        return user;
    }

    @GetMapping("/user/{id}")
    public Response getUserById(@PathVariable Long id){
        User user = userService.getOneUserName(id);
        return new Response(1, user);
    }

    @GetMapping("/config/test")
    public String getConfig(){
        log.info(env.getProperty("greetings"));
        log.info("value: " + hello);

//        List<String> names = Arrays.asList("gaiga", "gouni", "babo");
//        List<String> aName = names.stream().filter(n->n.contains("a")).collect(Collectors.toList());
        return env.getProperty("greetings");
    }
}
