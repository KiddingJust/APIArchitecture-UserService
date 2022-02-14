package com.example.user.dto;

import com.example.user.dto.member.MemberDto;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ResponseUserAndMember {

    private ResponseUser responseUser;
    private MemberDto memberDto;

}
