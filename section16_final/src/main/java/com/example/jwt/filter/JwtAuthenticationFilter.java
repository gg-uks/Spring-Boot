package com.example.jwt.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt.entity.Customer;
import com.example.jwt.entity.CustomerUser;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor                                   //  http://localhost:8081/login - username, password
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    // 로그인 시도!!
    private final AuthenticationManager authenticationManager;
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        System.out.println("JwtAuthenticationFilter : 로그인 시도중...");
        // AuthenticationManager --> PrincipalDetailsService 객체의 loadUserByUsername()메서드를 실행....
        // Authentication 객체생성 -> SecurityContextHolder(등록) --> View 사용 ~~~
        // JWT 토큰 생성
        // 1. form, 2. json(문자열) ---> IO(BufferedReader), ObjectMapper(객체=Customer)
        try{
            /*BufferedReader br=request.getReader();
            String input=null;
            while((input=br.readLine())!=null){
                System.out.println(input); // ?
            }*/
             ObjectMapper objectMapper=new ObjectMapper(); //JSON을 받아서
             Customer customer=objectMapper.readValue(request.getInputStream(), Customer.class); //다른 2기종시스템에서 날라온 데이터를 Customer 타입으로 받아서 데이터를 넣어줌
             System.out.println(customer); // ?
            //  username, password->
            UsernamePasswordAuthenticationToken authenticationToken=    //아이디와 패스워드를 UsernamePasswordAuthenticationToken 타입으로 만들어 줘야 한다
                    new UsernamePasswordAuthenticationToken(customer.getUsername(), customer.getPassword());
            // PrincipalDetailsService -> loadUserByUsername() 실행된다.
            Authentication authentication=authenticationManager.authenticate(authenticationToken);
            CustomerUser customerUser = (CustomerUser) authentication.getPrincipal();
            // 인증에 성공후 Authentication 개에 안에 저장된 정보를 얻어서 출력
            System.out.println(customerUser.getUsername());
            return  authentication; // SecurityContextHolder -> 권한처리
            // 데이터베이스 인증처리~~~ JWT 토큰 발행?
            //클라이언트에서 로그인 요청: 클라이언트는 로그인 요청을 보낼 때 사용자 이름과 비밀번호를 전송합니다. JwtAuthenticationFilter는 이 정보를 받아서 UsernamePasswordAuthenticationToken 객체로 만듭니다.
            //AuthenticationManager의 인증 처리: UsernamePasswordAuthenticationToken 객체는 AuthenticationManager에 전달됩니다. AuthenticationManager는 이 객체를 바탕으로 실제 인증을 처리하고, 인증된 사용자 정보를 Authentication 객체로 반환합니다.
            //인증 성공 후 JWT 발급: 인증이 성공하면 successfulAuthentication() 메서드가 호출되어, 인증된 사용자 정보로 JWT 토큰을 생성하여 클라이언트에 반환합니다.

        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    // attemptAuthentication() -->  successfulAuthentication() 호출
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // JWT 토큰 생성 -> 클라이언트에 헤더로 잔달을 해준다.
        System.out.println("successfulAuthentication 실행됨~~");
        CustomerUser customerUser= (CustomerUser) authResult.getPrincipal();

        String jwtToken= JWT.create()
                .withSubject("JWT 토큰")
                .withExpiresAt(new Date(System.currentTimeMillis()+(JwtProperties.EXPIRATION_TIME))) // 10분 만료시간 -> 고려?
                .withClaim("id", customerUser.getCustomer().getId())
                .withClaim("username", customerUser.getCustomer().getUsername())
                .sign(Algorithm.HMAC256(JwtProperties.SECRET)); // cosin 암호화
         response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);    // 포스트멘에서 확인
                                                      // Authorization(권한)
    }
}


//[클라이언트 로그인 요청]
//   ↓
//attemptAuthentication()
//   ↓ (성공하면)
//successfulAuthentication()
//   ↓
//JWT 토큰 발급 → 응답(Header에 Authorization 추가)





//클라이언트에서 로그인 요청: 클라이언트는 로그인 요청을 보낼 때 사용자 이름과 비밀번호를 전송합니다. JwtAuthenticationFilter는 이 정보를 받아서 UsernamePasswordAuthenticationToken 객체로 만듭니다.
//AuthenticationManager의 인증 처리: UsernamePasswordAuthenticationToken 객체는 AuthenticationManager에 전달됩니다. AuthenticationManager는 이 객체를 바탕으로 실제 인증을 처리하고, 인증된 사용자 정보를 Authentication 객체로 반환합니다.
//인증 성공 후 JWT 발급: 인증이 성공하면 successfulAuthentication() 메서드가 호출되어, 인증된 사용자 정보로 JWT 토큰을 생성하여 클라이언트에 반환합니다.
