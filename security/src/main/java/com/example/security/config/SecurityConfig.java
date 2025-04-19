package com.example.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {


    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder(){
        //회원 가입시 패스워드를 암호화 할때 사용
        return new BCryptPasswordEncoder();
    }


    //인증설정 SecurityFilterChain >>스프링 시큐리티도 체인이니까 여러개의 필터가 연결되어 있다
    @Bean                                               //클라이언트의 요청URL 정보를 http에 담아줌
    public SecurityFilterChain securityFilterChain(HttpSecurity http)throws Exception{//클라이언트로 부터 얻어온 인증요청 을 받는 클래스가 필요하다
        //권한 부여 규칙 정한다.
        http.authorizeHttpRequests(authz->authz //URL정보가 들어있음
             //   .requestMatchers("/ui/**").permitAll()//요청 매칭 /ui/** 인증없이 접근을 허용 하겠다 라는 뜻이다
             //   .requestMatchers("/admin/**").hasRole("ADMIN") //admin 이라는 요청을 접근하려면 ADMIN권한이 있는 사람만 접근이 가능하다 > 인증이 되어야 하고,인증된 사람중에서 ADMIN 권한인 사람만 요청을 허용
             //   .requestMatchers("/user/**").hasAnyRole("USER","ADMIN") //user경로 요청은 인증이 되야하고 USER 또는 ADMIN 두개의 권한중 하나만 되어도 접근 가능하다
             //   .anyRequest().authenticated()//나머지 모든 요청은 인증된 사람만 들어올수있다
                .requestMatchers("/api/**").authenticated() // /api/** 경로는 인증후 접근이 가능하다
                .requestMatchers("/books/**").authenticated()// /books/** 경로는 인증후 접근이 가능하다
                .anyRequest().permitAll() //나머지 모든 경로는 인증없이 접근을 허용함
        )
                .formLogin(form->form //> form > 로그인폼으로 전환시킬수 있는 클래스
                        .loginPage("/") //login 페이지 URL -> /login -> Controller -> View(customLogin.html)  //리다이렉션됨
                        // UsernamePasswordAuthenticationFilter : /loginProcess 라는 요청이오면 이 필터가 가로챈다 --> UserDetailsService(interface) 스프링이 만들어논 이클래스를 실행하게 되어있다 이쪽으로 제어권이 넘어간다 그래서 인터페이스를 implement를 해주는 클래스를 만들어 줘야 한다
                        .loginProcessingUrl("/loginProcess")// (URL 가로채서 :username ,password 정보를 읽어 DB연동함 ) 이URL로 넘어오면 스프링 시큐리티 로 제어권이 넘어간다 즉 제어권을 넘긴다 Spring Security API에서 처리
                        .defaultSuccessUrl("/") //로그인이 성공하면 이쪽 경로로 리다이렉션
                        .failureUrl("/?error=true") //로그인이 실패했을때 경로로 리다이렉션 -> error=true는 로그인 실패했을때 다시시도하라는 문구를 나타내기위한 작업이다
                        .permitAll()
                )
                .logout(logout->logout
                        .logoutUrl("/logout") //로그아웃 URL  -> 스프링 시큐리티가 가로채기 해서 로그아웃 처리를 해준다
                        .logoutSuccessUrl("/?logout=true") // /login URL로 가는데 로그아웃이 성공했습니다 라는걸 뿌려주기위해 쿼리스트링으로 보냄
                        .invalidateHttpSession(true) // 세션은 메모리에서 제거하는 용도다
                        .deleteCookies("JSESSIONID") // Java Session ID  클라이언트 브라우저에 이 이름으로 쿠키를 가지고있다 그래서 삭제를 해줘야함
                );

        return http.build();
    }
}
