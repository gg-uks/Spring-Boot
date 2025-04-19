package com.example.security.entity;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

public class CustomerUserDetails extends User { //  User(username,password,authorities) 사용자아이디와 사용자권한을 담아놓기위한 클래스이다

    private Customer customer; //지금 User 클래스안에는 세개의 정보만 들어있어서  로그인의 성공한 고객 정보에 다른정보들(이름,나이,이메일주소)도 담아줘야 하기때문에 Customer타입으로 받아줘야 한다

    public CustomerUserDetails(Customer customer) {                             //USER,MANAGER,ADMIN
        super(customer.getUsername(), customer.getPassword(), getAuthorities(customer.getRoles())); //지금 이 권한 을 넘겨주는 타입은  ollection<? extends GrantedAuthority> 이타입이여서 바꿔서 넘겨줘야 한다-->Spring Security의 User 클래스 생성자로 값 넘어감
        this.customer=customer; //private Customer customer;여기에 데이터 넣어줌
    }
    //String username, String password, Collection<? extends GrantedAuthority> authorities
    //Collection<? extends GrantedAuthority>  :  "GrantedAuthority를 구현한 클래스또는 상속한것 들 중 어떤 것이든 받을 수 있음."
    //인증된 사용자의 권한 정보를 GrantedAuthority 타입으로 관리
    //하지만 이건 인터페이스이기 때문에, 실제로는 이걸 구현한 클래스가 필요하고,
    //그 중 대표적인 구현체가 바로 SimpleGrantedAuthority
    public static Collection<? extends GrantedAuthority> getAuthorities(Set<Role> roles){ //지금 roles에 저장되어있는 타입을 GrantedAuthority 타입으로 바꿔줘야 한다 -- USER,MANAGER,ADMIN
        return roles.stream()//roles 안에 있는 객체를 스트림으로 바꾸는 이유는 다른타입으로 바꾸기위해서 쓰는것이다 //바이트 열로 바꿔 하나씩 하나씩 다른타입으로 변경하기 위해서 스트림으로 바꾸는 것이다
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getName())) // .mapd은 각각의 정보를 반복하여 타입변환해주고 ->접두사 붙혀서 이름 생성 //ROLE_USER,ROLE_MANAGER,ROLE_ADMIN
                .collect(Collectors.toList()); //권한정보를 SimpleGrantedAuthority 타입으로 바꿔서 "ROLE_USER,ROLE_MANAGER,ROLE_ADMIN" 을 List에 넣어주는것이다 하나씩
    }

    public void setCustomer(Customer customer){
        this.customer=customer;
    }

    public Customer getCustomer(){
        return customer;
    }



//    순서	역할
//1️⃣	로그인 시도 (예: /loginProcess)
//2️⃣	스프링 시큐리티 필터가 요청 가로챔
//3️⃣	UserDetailsService.loadUserByUsername() 호출
//4️⃣	이 안에서 CustomerUserDetails 생성함 (→ User 생성자 호출됨)
//5️⃣	반환된 UserDetails를 가지고 Spring이 Authentication 객체 생성
//6️⃣	이때 authorities가 Authentication 내부에 들어감
//7️⃣	SecurityContextHolder에 인증된 사용자 정보가 저장됨

}
