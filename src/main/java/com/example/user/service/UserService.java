package com.example.user.service;

import com.example.user.dto.RequestUser;
import com.example.user.dto.Response;
import com.example.user.dto.ResponseUser;
import com.example.user.dto.ResponseUserAndMember;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.example.user.service.feign.MemberServiceClient;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly=true)
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final Environment env;
    private final RestTemplate restTemplate;
    private final MemberServiceClient memberServiceClient;

    @Transactional
    public User createUser(RequestUser requestUser){
        User user = User.createUser(requestUser.getEmail(), requestUser.getName(), passwordEncoder.encode(requestUser.getPwd()), requestUser.getMemberId());
        userRepository.save(user);
        return user;
    }

    public User getOneUserName(Long id){
        Optional<User> user = userRepository.findById(id);
        //String email, String name, String encryptedPwd, Long memberId
        User userResult = user.orElse(User.createUser("gaiga@hd", "아무개", "1234", 5L));
        return userResult;
    }

    public Response getAllUser(){
        List<User> userAll = userRepository.findAll();
        /* RestTemplate 사용
        List<ResponseUserAndMember> resultList = userAll.stream().map(u->
                new ResponseUserAndMember(
                        ResponseUser.createResponse(u)
                        ,restTemplate.exchange(String.format(env.getProperty("member-service.url"), u.getMemberId()), HttpMethod.GET, null, new ParameterizedTypeReference<MemberDto>() {}).getBody()))
                .collect(Collectors.toList());*/
        /* Feign Client 사용 */
        List<ResponseUserAndMember> resultList = userAll.stream().map(u->
                        new ResponseUserAndMember(
                                ResponseUser.createResponse(u)
                                ,memberServiceClient.getMembers(u.getMemberId())))
                .collect(Collectors.toList());
        int size = userAll.size();
        return new Response(size, resultList);
//        String memberUrl = String.format(env.getProperty("member-service.url"), memberId);
//        ResponseEntity<MemberDto> memberResultEntity = restTemplate.exchange(memberUrl, HttpMethod.GET, null, new ParameterizedTypeReference<MemberDto>() {});
//        MemberDto memberResult = memberResultEntity.getBody();
//        return userRepository.findAll();
    }

    //DB에서 User 정보를 가져와서 Spring Security의 User 객체로 반환.
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException(email);
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getEncryptedPwd(), true, true, true, true, new ArrayList<>());
    }

    public User getUserByEmail(String email){
        User user = userRepository.findByEmail(email);
        if(user == null){
            throw new UsernameNotFoundException(email);
        }
        return user;
    }
}
