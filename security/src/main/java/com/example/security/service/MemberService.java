package com.example.security.service;

import com.example.security.entity.Customer;
import com.example.security.entity.Role;
import com.example.security.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository; //디비처리하기위한 주입

    @Autowired
    private BCryptPasswordEncoder passwordEncoder; //시큐리티 컨피그 파일에 BCryptPasswordEncoder 클래스 를 주입받아 암호화 할때 사용한다

    @Autowired
    private RoleService roleService;

    //회원 가입 기능
    public Customer memberRegister(Customer customer){
        //패스워드 암호화
        String encodePassword = passwordEncoder.encode(customer.getPassword()); //BCryptPasswordEncoder타입의 변수를 선언해서 현재 클라이언트에서 넘긴 패스워드 정보를 인코딩 시켜 encodePassword 에 저장
        customer.setPassword(encodePassword); //인코딩된 패스워드 (암호화된 패스워드) 를 Customer.password에 넣어줌
        //2.권한부여(User) <- 기본적으로  (MANAGER,ADMIN) : member_roles: USER(1)
        Set<Role>roles=new HashSet<>();
        Role userRole = roleService.findByName("USER"); //RoleService에 JPA랑 연결되어 있는 USER를 직접 널어줘서 권한 정보를 가져온다 기본적으로 USER권한을 주기위해
        roles.add(userRole); //디비에서 USER가져온 정보를 Set타입에 Role 객체에 저장한다 즉    4 , USER 가 추가
        customer.setRoles(roles);//다시 고객 정보에 권한을 저장해준다 해주면 요거는 중간다리 테이블 역할하기 때문에 자동으로 회원가입시 조인테이블이 형성되서 정보가 들어간다
        return memberRepository.save(customer); //insert쿼리
    }


}
