package com.example.oauth2.controller;

import com.example.oauth2.entity.Member;
import com.example.oauth2.entity.PrincipalUser;
import com.example.oauth2.entity.Role;
import com.example.oauth2.repository.MemberRepository;
import com.example.oauth2.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class Oauth2Controller {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @GetMapping("/")
    public String index(){
        return "index"; // index.html(로그인 UI)
    }

    @GetMapping("/user")
    //                           세션에 만들어진 사용자 정보
    public String user(Authentication authentication,
                       //이 코드에서는 @AuthenticationPrincipal OAuth2User oAuth2User를 사용하여, OAuth2 로그인 후 자동으로 사용자 정보를 받아 처리하고 있습니다. 별도로 OAuth2UserService나 다른 서비스에서 이 정보를 직접 처리할 필요 없이, Spring Security가 OAuth2 인증 과정을 다 처리해주기 때문
                       // 구글인증후 사용자 정보를 받고 싶을때
                       @AuthenticationPrincipal OAuth2User oAuth2User,//@AuthenticationPrincipal OAuth2User oAuth2User를 사용하면, Spring Security가 이미 OAuth2 인증 과정을 완료하고 사용자의 정보를 세션에 저장한 상태에서 이 정보를 자동으로 주입합니다.
                       Model model){
        String provider="google";
        String providerId=oAuth2User.getAttribute("sub"); //101926511570168785716
        if(providerId == null){
            throw new RuntimeException("Provider ID (sub) missing");
        }
        String username=provider+"_"+providerId;
        Optional<Member> optional=memberRepository.findByUsername(username);
        Member member=null;
        if(optional.isPresent()){
            System.out.println("로그인을 이미 한적이 있습니다. 당신은 자동회원으로 가입 되었습니다.");
            member=optional.get();
            PrincipalUser principalUser=new PrincipalUser(member);
            Authentication new_authentication = new UsernamePasswordAuthenticationToken(principalUser, null, getAuthorities(member.getRoles()));  //Authentication 인터페이스의 구현체 중 하나로,사용자의 인증 정보를 담고 있는 객체
            SecurityContextHolder.getContext().setAuthentication(new_authentication); //세션 메모리 영역에다가 넣어주는 작업
//            model.addAttribute("member", member);
            return "redirect:/"; // 메인으로 전환
        }else{
            System.out.println("처음 OAuth2로 로그인을한 사용자 입니다.");
            String password=passwordEncoder.encode("임의의값");
            String uname=(String)oAuth2User.getAttribute("name");
            String email=(String)oAuth2User.getAttribute("email");
            member=new Member();
            member.setUsername(username);
            member.setPassword(password);
            member.setUname(uname);
            member.setProvider(provider);
            member.setProviderId(providerId);
            member.setEmail(email);
            model.addAttribute("member", member);
        }
        return "modifyuser"; // modifyuser.html(추가적인 정보를 받는 페이지)
    }

    @PostMapping("/saveUser")
    public String saveUser(@ModelAttribute("member") Member member) {
        System.out.println(member);
        Set<Role> roles=new HashSet<>(); // USER
        Role userRole=roleRepository.findByName("USER");
        roles.add(userRole);
        member.setRoles(roles);
        // Save the member information
        memberRepository.save(member);
        PrincipalUser principalUser=new PrincipalUser(member); // 정보가 필요해 디비에서 조회한 것으로 다시 생성
        // Create an Authentication object based on the user information obtained from Google OAuth2
        Authentication authentication = new UsernamePasswordAuthenticationToken(principalUser, null, getAuthorities(member.getRoles()));//다시 유저디테일을 구현한 유저클래스에있는 메서드를 사용하여 아이디 비밀번호 권한등록한다
        // Set the authenticated user in the security context
        SecurityContextHolder.getContext().setAuthentication(authentication);   //세션 메모리 영역에다가 넣어주는 작업
        // Redirect the user to a secured page
        return "redirect:/";
    }

    public static Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles){  //이 메서드도 권한정보를 업데이트 하기위한 메서드이다
        return roles.stream()
                .map(role-> new SimpleGrantedAuthority("ROLE_"+role.getName()))
                .collect(Collectors.toList());
    }
}