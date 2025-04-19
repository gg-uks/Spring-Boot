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
public class MemberUserDetailsService implements UserDetailsService { //스프링 내부에서 자동으로 실행함 loadUserByUsername을
    @Autowired
    private MemberRepository memberRepository;
    //Method
    @Transactional //DB세션을 유지하기 위해 적어야 한다 Customer customer = optional.get(); 로 계속 정보를 전달 해야하기 떄문이다
    @Override                           //사용자가 입력한 username,password
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("loadUserByUsername 실행~");
        //데이터베이스 에서 username정보를 가지고 온다
        Optional<Customer> optional =memberRepository.findByUsername(username);
        if(!optional.isPresent()){
            throw new UsernameNotFoundException("User not found with username");
        }
        Customer customer = optional.get(); //roles정보는(지연로딩이기때문에)가지고 오지 않는상태 이시점에서 optional.get(); 으로 가져옴 /Hibernate는 세션 (DB연결)컨텍스트

        /*
        HttpSession session = request.getSession();
        session.setAttribute("customer",customer);
        */
        return new CustomerUserDetails(customer);//비밀번호 검사까지 --> 리턴값이 SecurityContextHolder(HttpSession) 에 저장된다 그러면 이값을 가지고 뷰단에서 메모리에 저장되어있는 세션값을 가지고 인증성공여부 판단
    }
}
/*
SecurityContextHolder : UserDetails <-- interface
                            /
                            / implements  --> UserDetails 임플리먼트를 하면 스프링이제공하는 추상메서드가 너무 많아서
                            /
                            User
                            /상속 extends    -->  스프링에서 UserDetails 인터페이스를 구현해 놓은 클래스를 제공해 준다 그게바로 User클래스다 그래서 User클래스를 상속받아서 사용하면 된다
                        CustomerDetails(Customer)   <--클래스를 만들어준다
SecurityContextHolder(HttpSession)------------------------------------------------------------------------------------
    JSESSIONID:123456
    Authentication Object(인증객체)
   [UserDetails  ->  User(username,password,authorities)  ->  CustomerUserDetails(Customer)]
-----------------------------------------------------------------------------------------------------------------------
                |    JSP,Thymeleaf
           #authentication ->  Authentication   :                       #authentication.principal  <--우리객체를가리킴  <-- CustomerUserDetails(Customer)
           #authentication.name  :사용자 아이디 가져올때                    #authentication.principal.username
           #authentication.authorities -> 여러개의(권한) 형식 [ ]  배열구조       #authentication.principal.customer   <---CustomerUserDetails(Customer)이객체를 다 가져옴


 */
