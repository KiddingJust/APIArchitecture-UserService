package com.example.user.security;

import com.example.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class WebSecurity extends WebSecurityConfigurerAdapter {

	private final UserService userService;
	private final BCryptPasswordEncoder bCryptPasswordEncoder;
	private final Environment env;
	
	//일단 인증은 했다 치고 권한 부분만 설정
	///users 라는 preFix 붙은 url은 인증 없이 사용 가능하도록! 
	@Override
	protected void configure(HttpSecurity http) throws Exception {
		
		http.csrf().disable();
//		http.authorizeRequests()
//					.antMatchers("/users/**",
//								 "/user-service/**")
//					.permitAll();
		http.authorizeRequests().antMatchers("/actuator/**").permitAll();
		//인증이 된 상태에서만 통과시키도록.
		//-->정확히 말하면, 여기서는 login 성공 시에 토큰 발급해주는 것만 함.
		//인증 여부와 관계 없이 모든 곳에 허용 --> 허용 안하려면 어떻게 해야하는지도 체크 필요함.
		http.authorizeRequests()
				.antMatchers("/**")
				.permitAll()
//				.hasIpAddress("192.168.0.101")	//source ip
				.and()
				.addFilter(getAuthenticationFilter());
				
		http.headers().frameOptions().disable();
	}

	//마지막 단계임. AuthenticationFilter 적용하겠다는 것 정도의 의미.
	//앞에서 만든 AuthenticationFilter.java
	//--> AuthenticationFilter를 빈으로 등록해서 사용하는 게 아니라, Spring Security에서 인스턴스를 직접 생성해서 사용하고 있음.
	//authenticationManager() 이건 어떻게 생성한 것?? 암튼 이건 필터에 등록하기 위함임.
	private AuthenticationFilter getAuthenticationFilter() throws Exception {
		AuthenticationFilter authenticationFilter = new AuthenticationFilter(userService, authenticationManager());
		authenticationFilter.setAuthenticationManager(authenticationManager());
		
		return authenticationFilter;
	}

   //3. 데이터베이스의 pw(encrypted)와 사용자 입력 pw 비교작업. 
	@Override
	protected void configure(AuthenticationManagerBuilder auth) throws Exception {
		//여기서 DB의 email, password 정보와 입력받은 정보가 일치하는지 체크해줌.
		//UserService의 loadByUsername을 통해서 인증 진행함.
		auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
	}
	
	
}
