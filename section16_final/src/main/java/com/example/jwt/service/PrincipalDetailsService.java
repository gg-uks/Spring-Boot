package com.example.jwt.service;

import com.example.jwt.entity.Customer;
import com.example.jwt.entity.CustomerUser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.stereotype.Service;

@Service // 추가
public class PrincipalDetailsService implements UserDetailsService {
    @Autowired
    private CustomerService customerService;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Customer customer=customerService.findByUsername(username);
        return new CustomerUser(customer); // CustomerUser<---User
    }
}
/*
  ID, PWD --> /login --> UsernamePasswordAuthenticationFilter -->  PrincipalDetailsService(loadUserByUsername)

 */

//[일반 로그인]
//UsernamePasswordAuthenticationFilter
//    ↓
//AuthenticationManager
//    ↓
//UserDetailsService.loadUserByUsername()
//    ↓
// JWT 토큰발급
//    ↓
//DB에서 사용자 조회
//
//[구글 로그인]
//OAuth2LoginAuthenticationFilter
//    ↓
//AuthenticationManager
//    ↓
//OAuth2UserService.loadUser()
//    ↓
// JWT 토큰발급
//    ↓
//구글 서버에서 사용자 정보 조회