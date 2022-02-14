package com.example.user.service.feign;

import com.example.user.dto.member.MemberDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name="orderservice") //마이크로서비스 이름 명시. Eureka 쓰면 이렇게도 가능.
//@FeignClient(name="member", url="http://127.0.0.1:8080")
public interface MemberServiceClient {

    @GetMapping("/api/v3/member/{id}")
    MemberDto getMembers(@PathVariable Long id);
}
