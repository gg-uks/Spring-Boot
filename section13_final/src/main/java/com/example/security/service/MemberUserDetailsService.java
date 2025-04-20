package com.example.security.service;

import com.example.security.entity.Customer;
import com.example.security.entity.CustomerUserDetails;
import com.example.security.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class MemberUserDetailsService implements UserDetailsService {

    @Autowired
    private MemberRepository memberRepository;
    // Method
    @Transactional
    @Override                                          // 사용자가 입력한 username, password
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername 실행~~");
        // 데이터베이스에서 username의 정보를 가지고 온다.
       Optional<Customer> optional =memberRepository.findByUsername(username);
       if(!optional.isPresent()){
            throw new UsernameNotFoundException("User not found with username:"+username);
       }
       Customer customer=optional.get(); // roles 정보는 가죠오지 않는 상태 이지점에서 가져옴 / Hibernate는 세션(DB연결)컨텍스트
       /*
            HttpSession session=request.getSession();
            session.setAttribute("customer", customer);  ${customer.username}
        */
        return new CustomerUserDetails(customer);  // 비밀번호 검사 ---> SecurityContextHolder(HttpSession)      |    DaoAuthenticationProvider.authenticate()가 비밀번호 비교해줌  리턴타입이 UserDetails인터페이스 타입이다 타입이 안맞아서 부모자식 관계를 형성해 넣어주면된다
    }
}
/*
 SecurityContextHolder :   UserDetails <--interface
                                                             |
                                                             |  implements
                                                             |
                                                        User
                                                             | extends
                                             CustomerUserDetails(Customer)
   SecurityContextHolder(HttpSession) ----------------------------------------------------
                JSESSIONID : 123456
                Authentication Object(인증객체)
                [UserDetails -> User(username,password,authorities) -> CustomerUserDetails(Customer)]
     -----------------------------------------------------------------------------------
                                       | JSP, Thymeleaf                                                                                |
                           #authentication   ->Authentication                                 #authentication.principal
                           #authentication.name(사용자아이디)                                  #authentication.principal.username
                           #authentication.authorities -> 여러개 : [     ]                   #authentication.principal.customer
 */
