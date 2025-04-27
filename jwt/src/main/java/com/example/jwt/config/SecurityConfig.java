package com.example.jwt.config;

import com.example.jwt.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class SecurityConfig {

    @Autowired
    private CorsConfig corsConfig;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        //JWT SERVER 구현  : React.js 프론트를 구성(로그인 UI X, 인증방식(JWT토큰방식)-ID,PWD,세션X)
        //서버(백엔드)<--CORS(정책)-->서버(React.js)
        //시큐리티는 (form-CSRF 토큰을 넘기게 되어있음  JWT사용할때는 CSRF 토큰을 넘기지 않겠다)
        http.csrf(csrf->csrf.disable()) // JWT사용할때는 CSRF 토큰을 넘기지 않겠다
                .sessionManagement(sm->sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))//세션을 사용하지 않겠다
                .formLogin(form->form.disable()) // 폼로그인도 사용하지 않겠다
                .httpBasic(hb->hb.disable()) //아이디 패스워드 방식으로 사용하지 않겠다라는 뜻 JWT토큰을 사용해야 하니까
                .authorizeHttpRequests(authz->authz
                        .requestMatchers("/api/v1/user/**").hasAnyRole("USER","MANAGER","ADMIN")//"/api/v1/user/**" 오는 모든요청은  USER  MANAGER ADMIN 권한을 가진 사람만 들어올수 있다
                        .requestMatchers("/api/v1/manager/**").hasAnyRole("MANAGER","ADMIN")
                        .requestMatchers("/api/v1/admin/**").hasAnyRole("ADMIN")
                        .anyRequest().permitAll()   //이외에 요청은 인증없이도 누구나 들어올수 있다
                )
                //CORS(정책) 을 풀어줘야 한다 - 필터를 가지고 있는 객체를 만들기 --이렇게 필터등록도 되는데 여러가지의 필터를 등록하면 밑에나와있는 DSL 방식으로 하면 좋다
                //.addFilter(corsFilter()); //필터를 등록한것이다 crosFilter() 메서드를  -- 등록하는 이유는 아이디 패스워드를 받아서 스프링 필터체인에서 제공해주는 API를 수동으로 실행시켜주기 위해서는  이필터를 등록해서 필터에서 객체를 생성해주는것이다
                //apply로 MyCustomerDSL 객체로 묶어서 보냈는데 6.2버전이후로는 apply를 몼써서 에드 필터로 처리해야한다
                //id 'org.springframework.boot' version '3.1.5' // 3.3.11 → 3.1.5로 낮춰서 써야 한다
                .apply(new MyCustomerDSL());

        return http.build();
    }
   //사용자 정의 DSL(Domain Specific Language)  보안구성을 직관적으로 쓸수있게 해주는 것 문법
    public class MyCustomerDSL extends AbstractHttpConfigurer<MyCustomerDSL,HttpSecurity>{ //MyCustomerDSL:현재클래스  HttpSecurity:확장시킬것

       @Override
       public void configure(HttpSecurity http) throws Exception { //이메서드로 보안정책을 좀더 확장시키는 것이다
           AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManager.class); //Spring Security는 서버가 켜질 때, 내부적으로 AuthenticationManager를 자동으로 만듬 ->Spring Security가 알아서 준비해놓은 AuthenticationManager를 꺼내쓰는 것.
           http.addFilter(corsConfig.corsFilter()) //이렇게 하면 등록이 되는것이다 이렇게쓰는이유는 부가적인 것도 설정할수있어서 유연적이다
           //추가적인 필터 2개를 등록..
                                                //AuthenticationManager = 아이디 비밀번호를 검증해주는 놈
//                                              근데 JWT는 세션을 사용하지 않음.
//                                                   ➔ 대신 토큰을 주고받으면서 인증을 해야 해.
                                                //JwtAuthenticationFilter 안에서 로그인 처리(=인증) 를 수동으로 해줘야 하기때문에 .
           .addFilter(new JwtAuthenticationFilter(authenticationManager)); // AuthenticationManager -> 로그인할 때 아이디, 비번이 맞는지 사용자 존재 여부 패스워드 일치 여부 이런 인증 로직을 담당하는 핵심 객체   -- 지금 이건 생성자 주입방법이여서 객체가 생성될떄 바로 주입 되는 방식이다

       }
   }
//spring Boot는 SecurityConfig 클래스에서 정의된 필터들을 스프링 컨텍스트에 등록하고, 이 필터들은 톰캣 서버가 요청을 받기 전에 메모리 상에 로드
}


