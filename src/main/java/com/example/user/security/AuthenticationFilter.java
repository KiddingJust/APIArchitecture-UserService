package com.example.user.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.user.dto.RequestLogin;
import com.example.user.service.UserService;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthenticationFilter extends UsernamePasswordAuthenticationFilter{

	private final UserService userService;
	private final AuthenticationManager authenticationManager;

	//인증 요청하는 부분. 일단 Filter이므로 HttpServletRequest로 받아야 함.
	@Override
	public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
			throws AuthenticationException {
		try {
			//1. 사용자 요청 정보를 RequestLogin 객체로 생성.
			RequestLogin creds = new ObjectMapper().readValue(request.getInputStream(), RequestLogin.class);
			//2. RequestLogin 를 토대로, AuthenticationManager에 인증 작업 요청(email, pw 맞는지 확인).
			return getAuthenticationManager().authenticate(
						new UsernamePasswordAuthenticationToken(
								creds.getEmail(), 
								creds.getPwd(),
								new ArrayList<>()
						)
					);
		} catch(IOException e) {
			throw new RuntimeException(e);
		}
	}

	//로그인 성공 후의 로직
	@Override
	protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain,
			Authentication authResult) throws IOException, ServletException {
		// 토큰 만료 시간 등을 설정하는 부분.
		//SpringSecurity의 User 객체로 캐스팅
		String userName = ((User) authResult.getPrincipal()).getUsername();
		//Email 정보로 토큰 만드는 걸로 변경할 것. 그전에는 UserId로 해준 듯함.
		com.example.user.entity.User userDetails = userService.getUserByEmail(userName);

		//로그인 성공 시 JWT 생성
		String token = Jwts.builder()
							.setSubject(userDetails.getEmail())
							.setExpiration(new Date(System.currentTimeMillis() + 
											Long.parseLong("86400000")))
							//사용할 알고리즘과 signature에 들어갈 secret 값 세팅
							.signWith(SignatureAlgorithm.HS512, "ecommerce_token")
							.compact();
		
		response.addHeader("token", token);
		response.addHeader("user email", userDetails.getEmail());
	}
	
	
}
