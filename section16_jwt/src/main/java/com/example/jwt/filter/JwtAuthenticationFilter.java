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
import org.springframework.security.core.GrantedAuthority;
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
        // 1. form, 2. json(문자열) --> IO(BufferedReader), ObjectMapper(객체=Customer)
        try{
            /*BufferedReader br=request.getReader();
            String input=null;
            while((input=br.readLine())!=null){
                System.out.println(input); // ?
            }*/
            // 🔽 JSON 요청 바디에서 직접 username, password 파싱

            ObjectMapper objectMapper=new ObjectMapper();
             Customer customer=objectMapper.readValue(request.getInputStream(), Customer.class);
             System.out.println(customer); // ?
            //  username, password->

            // 🔽 직접 Authentication 토큰 생성해서 인증 시도
            UsernamePasswordAuthenticationToken authenticationToken=
                    new UsernamePasswordAuthenticationToken(customer.getUsername(), customer.getPassword());
            // PrincipalDetailsService -> loadUserByUsername() 실행된다.
            Authentication authentication=authenticationManager.authenticate(authenticationToken);
            CustomerUser customerUser = (CustomerUser) authentication.getPrincipal();
            // 인증에 성공후 Authentication 개에 안에 저장된 정보를 얻어서 출력
            System.out.println(customerUser.getUsername());
            return  authentication; // SecurityContextHolder -> 권한처리
            // 데이터베이스 인증처리~~~ JWT 토큰 발행?
        }catch(Exception e){
            e.printStackTrace();
        }
        return null;
    }
    // attemptAuthentication() -->  successfulAuthentication() 호출
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // JTW 토큰 생성 -> 클라이언트에 헤더로 잔달을 해준다.
        System.out.println("successfulAuthentication 실행됨~~");
        CustomerUser customerUser= (CustomerUser) authResult.getPrincipal();
        String jwtToken= JWT.create()
                .withSubject("JWT 토큰")
                .withExpiresAt(new Date(System.currentTimeMillis()+(JwtProperties.EXPIRATION_TIME))) // 10분 만료시간 -> 고려?
                .withClaim("id", customerUser.getCustomer().getId())
                .withClaim("username", customerUser.getCustomer().getUsername())
                //권한정보 (List -> Array -> String [] 로 바꿔야 한다
                .withArrayClaim("authorities", customerUser.getAuthorities().stream()// 권한 목록을 스트림으로 바꿔서 다른형으로 바꿀때는 스트림을 써야 한다
                        .map(GrantedAuthority::getAuthority)//각 권한 객체에서 문자열만 꺼냄. 즉, "ROLE_USER", "ROLE_ADMIN" 같은 문자열만
                        .toArray(String[]::new) // [ ROLE_USER, ROLE_MANAGER, ROLE_ADMIN  ] // 그걸 문자열 배열로 변환
                )
                .sign(Algorithm.HMAC256(JwtProperties.SECRET)); // cosin 암호화
         response.addHeader(JwtProperties.HEADER_STRING, JwtProperties.TOKEN_PREFIX+jwtToken);    // 포스트멘에서 확인
                                                      // Authorization(권한)
    }
}

//메서드	설명
//attemptAuthentication()	로그인 시도 시 호출 (ID/PW 검증 등)
//successfulAuthentication()	로그인 성공 후 호출되어, 보통 토큰 발급 등 처리
//unsuccessfulAuthentication()	로그인 실패 시 호출되어, 실패 응답 등 처리

//✅ 프론트엔드(클라이언트)에 전달되는 JWT 구조:
//JWT는 기본적으로 3가지 파트로 구성된 문자열
//<헤더>.<페이로드>.<서명>
//파트	포함 여부	설명
//Header	✅ 포함됨	어떤 알고리즘(HMAC256 등)으로 서명했는지 정보가 있음
//Payload	✅ 포함됨	유저 정보, 권한, 만료일 같은 데이터(JSON 형태)
//Signature	✅ 포함됨	Header + Payload를 서버의 시크릿 키로 서명한 값
//Secret Key	❌ 포함되지 않음 ❗	서버만 보유함, 절대 클라이언트에 전달되지 않음

//서명이란?
//서명은 헤더 + 페이로드를 기반으로 생성되며, 클라이언트가 서버에서 받은 JWT의 서명(Signature)을 검증할 때 시크릿 키를 사용하는 것입니다.
//예시
//서버: header, payload를 사용해 signature를 만듭니다.
//
//클라이언트: header.payload.signature를 서버에 전달합니다.
//
//서버: header, payload와 서버의 시크릿 키로 서명을 생성하고, 클라이언트가 보낸 서명과 일치하는지 확인합니다.

//data + secretKey → SHA256으로 해시 → 서명(signature) 생성
//서버는 JWT를 만들 때 서명(signature)을 생성하고, 클라이언트에서 다시 들어올 때도 _같은 방식으로 서명을 생성해서 비교_합니다.



//🔁 JWT 서명 비교 흐름 (서버에서)
//✅ [1] JWT 생성 (로그인 시, 서버가 함)
//1. header, payload를 base64로 인코딩
//2. base64(header) + "." + base64(payload) → 메시지
//3. 메시지에 secretKey로 HMAC-SHA256 처리 → signature 생성
//4. 최종 JWT: base64(header).base64(payload).signature

//✅ [2] JWT 검증 (요청 들어올 때, 서버가 다시 함)
//1. 클라이언트로부터 JWT를 받음 → header.payload.signature
//2. 서버가 header + payload 부분을 다시 가져와서,
//  동일한 secretKey로 HMAC-SHA256 처리해서 **signature 계산**
//3. 클라이언트가 보낸 signature와 **일치하는지 비교**
//같으면 → 토큰 무결성 OK → 신뢰할 수 있음
//다르면 → 누군가 내용 조작했거나 유효하지 않은 토큰 → 거부

//💡 핵심
//클라이언트는 signature만 알고 있고, secretKey는 모름
//그래서 위조할 수 없고, 서버는 토큰 내용이 변조되지 않았는지 검증 가능