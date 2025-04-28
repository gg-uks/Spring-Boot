package com.example.jwt.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.example.jwt.entity.Customer;
import com.example.jwt.entity.CustomerUser;
import com.example.jwt.service.CustomerService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;
//여기는 필터라 오토와이어드 서비스를 못한다 DB정보를 가지고 와야하는데 그작업을 못해서 시큐리티 컨피그 파일에서 가져와 매개변수로 넣어줘야 한다 -시큐리티 컨피그 파일에서-

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {//JwtAuthorizationFilter 이 필터를 시큐리티 컨피그에 등록 해야한다      //BasicAuthenticationFilter-> 클라이언트로 부터 넘어온 URL 은 이인증 필터를 타게되어있음

    private CustomerService customerService;

    //생성자 주입을 통한 서비스를 넘겨줘야 한다 디비정보를 가지고 오려면
    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,CustomerService customerService) {//authenticationManager 인증매니저를 받아 부모로 넘겨야한다
        super(authenticationManager);
        this.customerService=customerService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException { //모든요청이 다 여기를 다 타고 건너가는데 (JWT토큰을 가지고 넘어간다)
        System.out.println("인증이나 권한이 필요한 요청들은 이부분을 실행");
        //JWT토근을 받아보자,
        String jwtHeader = request.getHeader("Authorization"); //토큰은 Authorization 키값으로 넘어온다
        System.out.println("jwtToken"+jwtHeader);//JWT 헤더에 있는 토근이 나옴

        if(jwtHeader==null || !jwtHeader.startsWith("Bearer")){
            chain.doFilter(request,response); //if조건문이 참이면 다음 필터로 건너뛰게 한다 JWT토큰이 없거나 접두사가 Bearer이아니면
            return;
        }
        //정상적인 JWT 토큰이면..
        String jwtToken = request.getHeader("Authorization").replace("Bearer","");//replace("Bearer","") JWT 토큰정보를 Bearer를 빈값으로 바꿔서 실제 토큰값을 가지고온다
        //cosin
        // 2. JWT 라이브러리를 사용해서 토큰을 검증하고,
//    정상적이면 토큰 안에 들어 있는 "username" 이라는 claim 값을 꺼낸다
        String username = JWT.require(Algorithm.HMAC256(JwtProperties.SECRET))// (1) 시크릿 키를 이용해서 (HMAC256 알고리즘을 씀) -> 서버에서 미리 정해둔 비밀키(SECRET) 로 JWT를 검증할 준비를 하는 것
                .build()// (2) JWT 검증 객체를 만들고
                .verify(jwtToken) // (3) 받은 jwtToken을 검증한다
                .getClaim("username") // (4) 토큰 안에 들어있는 "username" 클레임을 가져온다
                .asString(); // (5) 그 값을 문자열로 변환한다
        if(username!=null){
            Customer customer = customerService.findByUsername(username);//DB나 서비스에서 username에 해당하는 Customer 엔티티를 조회한다
            CustomerUser customerUser = new CustomerUser(customer); //  이객체가 메모리에 올라간다 //스프링 시큐리티가 요구하는 UserDetails 객체로 포장한다.
            Authentication authentication = new UsernamePasswordAuthenticationToken(customerUser,null,customerUser.getAuthorities()); //권한정보와 사용자 정보를 만듬 //로그인 완료를 의미하는 Authentication 객체를 직접 만든다.첫 번째 인자: 사용자 정보, 두 번째 인자: 비밀번호 (우리는 JWT니까 필요없어서 null), 세 번째 인자: 권한 정보(roles)
            SecurityContextHolder.getContext().setAuthentication(authentication); //스프링 시큐리티 컨텍스트 홀더에 저장 하는것이다  //현재 스레드(SecurityContext)에 인증 객체를 집어넣는다. → 즉, "이 요청은 인증된 사용자야" 라고 등록하는 것!
        }
    }
}



//클라이언트 → JWT 토큰 보내줌 → 서버

//1. JWT 토큰 유효성 검사
//2. 토큰 안에서 username 꺼냄
//3. username으로 DB에서 사용자 정보 조회
//4. UserDetails 형태로 감쌈
//5. Authentication 객체 생성
//6. SecurityContext에 Authentication 등록

//결과 : 요청한 사용자가 "인증된 사용자"로 인식됨
