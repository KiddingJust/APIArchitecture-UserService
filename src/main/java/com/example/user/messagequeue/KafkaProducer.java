package com.example.user.messagequeue;

import com.example.user.dto.RequestUser;
import com.example.user.dto.ResponseUser;
import com.example.user.entity.User;
import com.example.user.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class KafkaProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final UserRepository userRepository;

    @Transactional //변경감지 써야하므로 영속성 컨텍스트 필요
    public ResponseUser sendUpdateUser(String topic, RequestUser requestUser){
        ObjectMapper mapper = new ObjectMapper();
        String jsonRequest = "";
        try {
            jsonRequest = mapper.writeValueAsString(requestUser);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        kafkaTemplate.send(topic, jsonRequest);
        //변경감지로 User 엔티티도 변경
        User user = userRepository.findByMemberId(requestUser.getMemberId());
        user.updateUser(requestUser);
        ResponseUser response = ResponseUser.createResponse(user);
        return response;
    }
}
