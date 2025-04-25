package com.example.oauth2.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{
      http.authorizeHttpRequests(authz->authz
              .requestMatchers("/","/home").permitAll() //이 두개의 경로는 인증없이 요청가능
              .anyRequest().authenticated() // 다른경로는 반드시 인증거쳐야 한다
      )
              //[구글로그인] http://localhost8081[/oauth2/authorization/google] ->이요청이 오면 스프링 시큐리티가 .oauth2Login()이메서드가 가로챈다   -> 구글로 로그인을 시도
              //만약에  http://localhost8081/member 요청->로그인? 해야한다
              .oauth2Login(oauth2->oauth2
                      .loginPage("/") //요걸 설정해줘야 구글로 로그인 요청을 할수있다. 루트 페이지에 구글 로그인 버튼이 있다면 ➝ 구글 로그인 시도   http://localhost8081/member식으로 넘어오면 로그인 페이지로 이동시킴
                      .defaultSuccessUrl("/") //구글로그인에 성공한다면 요 경로로 가라는뜻
                      .permitAll()//혹시나 대비해 /home로 갈때 url접근이 안될수도 있으니 설정하는것이다. 무한루프 발생 가능성 도 있음
              )
              .logout(logout->logout
                      .logoutUrl("/logout")
                      .logoutSuccessUrl("/home")
                      .invalidateHttpSession(true)
                      .deleteCookies("JSESSIONID")
              );
      return http.build(); // ->SecurityFilterChain 에 넣어주면서 빌드해준다 리턴타입
    }


}
