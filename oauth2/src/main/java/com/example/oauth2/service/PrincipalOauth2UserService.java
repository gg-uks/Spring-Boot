package com.example.oauth2.service;

import com.example.oauth2.entity.Member;
import com.example.oauth2.entity.PrincipalUser;
import com.example.oauth2.entity.Role;
import com.example.oauth2.repository.MemberRepository;
import com.example.oauth2.repository.RoleRepository;
import jakarta.servlet.ServletOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Service
public class PrincipalOauth2UserService extends DefaultOAuth2UserService { //구글이 우리에게 데이터를 내려보내줄때 실행되는 서비스

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private MemberRepository memberRepository;
    //[구글로그인] http://localhost8081[/oauth2/authorization/google] ->이요청이 오면 스프링 시큐리티가 .oauth2Login()이메서드가 가로챈다   -> 구글로 로그인을 시도
    // 구글 로그인 성공시-> OAuth2UserRequest 요객체를 생성
    @Transactional
    @Override//DefaultOAuth2UserService 에 있는 loadUser메서드를 오버라이딩 해야한다 구글에서 내려주는정보는 (OAuth2UserRequest userRequest) 이안에있다 구글에서 인증하고 인증한 데이터를 받아오는 작업
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException { //구글 로그인 성공시 OAuth2UserRequest userRequest 여기에 정보가 담김
        System.out.println("userRequest" + userRequest); // 구글에서 로그인하면 구글로 부터 데이터를 보내주는 값을 확인 메서드가 자동실행되나 확인
        // userRequestorg.springframework.security.oauth2.client.userinfo.OAuth2UserRequest@18c75a03  이값이 날라오는것 확인
        System.out.println("userRequest" + userRequest.getClientRegistration()); //어떤서버의 요청을 했는지 - google정보가 날라올것
        System.out.println("userRequest" + userRequest.getAccessToken().getTokenValue()); // 우리가 클라이언트가 구글에 로그인하면 인증이 성공했다라는 정보를 토큰으로 보내줌  근데 이토큰을 가지고있다가 다시 구글에게 다시준다 왜냐하면 구글한테 이메일주소나 다른프로파일 정보를 얻으려면 다시 토큰을 보내야 한다 -인증이성공한 토큰 비교하기위해-
        System.out.println("userRequest" + super.loadUser(userRequest).getAttributes());// super.loadUser(userRequest) 이 넘긴값은 무슨말이냐 부모클래스에서 Acess토큰을 가지고 내부적으로 다시 요청한다 구글에 엑세스토큰-->요청 <-------- email.profile---->

        //강제로 히원가입을 진행하기위한 정보를 추출
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId(); //google
        String providerId = oAuth2User.getAttribute("sub"); //108176089712318726983
        String username  = provider + "_" + providerId;
        String password = passwordEncoder.encode("임의의값");
        String uname = (String) oAuth2User.getAttribute("name"); //name:홍길동
        String email = (String) oAuth2User.getAttribute("email");

        Set<Role>roles = new HashSet<>(); //USER권한 주기
        Role userRole = roleRepository.findByName("USER");
        roles.add(userRole);

        //데이터베이스의 데이터를 저장하기전에 가입유무를 확인
        Optional<Member>optional = memberRepository.findByUsername(username);
            Member member = null;
                if(optional.isPresent()){
                    System.out.println("로그인을 이미 한적이 있습니다 . 당신은 자동회원 가입이 되었습니다");
                    member = optional.get();
                }else{
                    System.out.println("처음 Oauth2 로그인을 한 사용자 입니다");
                    member = new Member();
                    member.setUsername((username));
                    member.setPassword(password);
                    member.setUname(uname);
                    member.setProvider(provider);
                    member.setProviderId(providerId);
                    member.setEmail(email);
                    member.setRoles(roles);

                    memberRepository.save(member); //회원가입
                }

                return new PrincipalUser(member); //세션으로 만들어진다
                //로그인 성공 -> 세션(SecurityContextHolder)<-------member
                        // Oauth2User(interface)
        //return member; //  UserDetails-->User(class)-->CustomerMember    부모 한테 리턴 해줘야 함 그러면 엑세스 토큰가지고 내부적으로 구글에게 다시 요청을 해서 프로파일 정보들 이메일정보등 다 가자기고온다 DefaultOAuth2UserService 상속한 클래스에서

        /*userRequestClientRegistration{registrationId='google', clientId='814625681686-89q8n8q0emdaeq93deujkrvmf5r5go24.apps.googleusercontent.com', clientSecret='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', clientAuthenticationMethod=client_secret_basic, authorizationGrantType=org.springframework.security.oauth2.core.AuthorizationGrantType@5da5e9f3, redirectUri='{baseUrl}/{action}/oauth2/code/{registrationId}', scopes=[email, profile], providerDetails=org.springframework.security.oauth2.client.registration.ClientRegistration$ProviderDetails@1def598c, clientName='Google'}
        userRequestya29.a0AZYkNZjVP6sqqoazbhXVFv-RPBQUGHC8Q78XkVrwsCSF4N7KzvpGJ1ghM4GuhzR-KVpSj22CZIqWtsw8s_SS6Z2piDTXD6tRoUDO1TmEeRhIneT8jfLQsIKJEOvMFOFxXXOrPqv1_vNOhdIS0X9Moi5Y1tc_3fsDKVXGX-OQaCgYKAZkSARMSFQHGX2MiYnYqXi0qAOkAKYZH2A8QBQ0175  -> 토큰값
        userRequest{
         sub=108176089712318726983,
         name=김남국,
         given_name=남국,
         family_name=김,
         picture=https://lh3.googleusercontent.com/a/ACg8ocKln8MW-mexJ9FXLa6m8_YXjmraqfuXM0b_lFPa3cCvlxS2jA=s96-c,
         email=skarnr659876@gmail.com,
        email_verified=true}
        강제로 회원가입을 시키기 위해서: Member
        */


    }
}
