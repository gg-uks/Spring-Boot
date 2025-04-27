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
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

@RequiredArgsConstructor //생성자 주입
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter { //이 필터를 사용자 정의 필터로 구현한다->    //UsernamePasswordAuthenticationFilter 이 필터는 기본적으로 POST 요청에서 ID와 비밀번호를 추출하고, 이를 바탕으로 인증을 시도하는 방식으로 작동  //UsernamePasswordAuthenticationFilter는 폼 로그인에서 사용되는 기본적인 인증 필터다 Spring Security에서는 기본적으로 ID와 비밀번호를 사용해서 인증하는 방식이 많기 때문에, 이 클래스를 상속받으면 로그인 처리에 필요한 기본적인 동작을 이미 구현해놓은 상태에서 확장할 수 있음
    //로그인 시도 ~
    //attemptAuthentication 메서드가 자동으로 실행된다 로그인을 하면

    private final AuthenticationManager authenticationManager;


    @Override//attemptAuthentication 메서드는 기본적으로 /login URL에 대해 호출 이는 UsernamePasswordAuthenticationFilter의 기본 동작이다, 로그인 시도 시 해당 URL로 POST 요청을 보내면 이 메서드가 자동으로 실행된다.
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException { //attemptAuthentication() 메서드는, 필터에서 실제로 로그인 인증을 시도하는 역할을 함
        System.out.println("JwtAuthenticationFilter:로그인 시도");

        //AuthenticationManager -> PrincipalDetailsService 요 객체의 loadUserByUsernam()메서드를 실행...
        //Authentication 객체생성 -> Security ContextHolder(등록)  --> view 사용~~~
        //JWT 토큰생성
        //1.form , 2.JSON(문자열)으로 넘어오는 방식이있는데 두가지경우를 다 처리해야한다 -->IO로받는방법(BufferedReader),ObjectMapper(객체=Customer)로 받을수 있다
        try {
//            BufferedReader br=request.getReader(); //리퀘스트에서 스트림을 얻어서 그스트림을 이용해 넘어온 데이터를 읽음
//            String input = null;
//            while ((input=br.readLine())!=null){//한 라인별로 읽어서 널이 아니면 실행
//              System.out.println(input); //username=1111&password=1111*/
//            }
        ObjectMapper objectMapper = new ObjectMapper();
        Customer customer = objectMapper.readValue(request.getInputStream(), Customer.class);//요청으로 들어오는 걸 인풋스트림으로 읽어서 Customer클래스로 바꿔준다 자동으로 JSON이 Customer이랑 매핑이 된다 -> InputStream은 "데이터 읽기"만 하고, ObjectMapper가 "JSON → Java 객체 매핑"을 한다. ->변환된 Customer 객체를 담아서, 이후 코드에서 아이디/비밀번호 같은 데이터 꺼내 쓸 수 있다.
            System.out.println(customer); //Customer(id=null, username=guk, password=1234, roles=null)
            //username과 password ->
            UsernamePasswordAuthenticationToken authenticationToken= // 스프링 시큐리티가 내부적으로 필터에서 인증하려면 기본적으로 username password가 UsernamePasswordAuthenticationToken 이타입으로 만들어져야 한다
                    new UsernamePasswordAuthenticationToken(customer.getUsername(),customer.getPassword());
            // authenticate == PrincipalDetailsService implements UserDetailsService loadUserByUsername() 을 실행한다
        Authentication authentication =authenticationManager.authenticate(authenticationToken);// loadUserByUsername() 이 실행되 인증이 성공적으로 마치면 Authentication 객체가 만들어진다

            //인증에 성공후 authentication 객체안에 저장된 정보를 얻어서 출력을 테스트하기위한 작업
            CustomerUser customerUser = (CustomerUser) authentication.getPrincipal();
            System.out.println(customerUser.getUsername());

            return authentication; //세션에 등록하기 위하여 ->SecurityContextHolder 에 저장  -> 권한 처리
            //데이터 베이스 인정처리 --- JWT토큰 발행
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

//    attemptAuthentication 가 정상적으로 실행후에 successfulAuthentication 이 자동으로 호출이 된다
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        //JWT 토큰을 생성후  -> 생성된 토큰을 클라이언트에 헤더로 전달을 해준다.
        System.out.println("successfulAuthentication 실행됨");
        CustomerUser customerUser = (CustomerUser) authResult.getPrincipal(); //인증된 사용자를 가지고오는 작업
        String jwtToken = JWT.create()
                .withSubject("JWT 토큰입니다") //JWT제목                                               //이렇게 아래에 나온것처럼 인터페이스를 만들어서 변수를 만들어 가져와서쓰면 좀더 효율적이다
                .withExpiresAt(new Date(System.currentTimeMillis()+(JwtProperties.EXPIRATION_TIME))) //만료시간 -> 고려 해야함 만료시간이 10분이라
                        .withClaim("id",customerUser.getCustomer().getId())//실제 데이터 설정
                        .withClaim("username",customerUser.getCustomer().getUsername())
                        .sign(Algorithm.HMAC256(JwtProperties.SECRET)); // 이 키값으로 -> → JWT 토큰을 id, username 정보를 담아서 만든 후 cosin이라는 비밀 키로 서명(sign)함.  -> 이키값을 암호화 시켜서 보내는게 효율적
        response.addHeader(JwtProperties.HEADER_STRING , JwtProperties.TOKEN_PREFIX+jwtToken);//클라이언트에게 토큰 전달하는작업 ->만든 토큰을 HTTP 응답 헤더에 실어서 클라이언트에게 보내줌.

        //헤더에 보면 이렇게 응답된것을 볼수 있다.
//Authentication : Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJKV1Qg7Yag7YGw7J6F64uI64ukIiwiaWQiOjIsImV4cCI6MTc0NTcyNjU5OCwidXNlcm5hbWUiOiJndWsifQ.zwsW8OsEv0fU4YE88ta2PQBSp-bn_d4VIG3BQoEmbY4

    }
}
