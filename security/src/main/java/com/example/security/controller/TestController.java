package com.example.security.controller;

import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@EnableMethodSecurity(securedEnabled = true,prePostEnabled = true) //이거를 적용해야 @Secured 키워드안에 권한정보를 읽어서 활성화 해주는것이다 이거를 안적으면 바로 리턴값이 나온다 권한정보를 안보고
public class TestController {                                       //prePostEnabled = true 이렇게 해야 두가지 권한을 읽게 해줄수 있다.

    //이렇게 하나또는 두개로 세부적으로 설정하고 싶으면 이렇게 해야하고 아니면 특정하나의 url밑으로 오는경로로 다걸고 싶으면 시큐리티 컨피크 클래스 파일에서 설정해주면 된다

    @Secured("ROLE_MANAGER") //권한정보까지 보고 핸들러해준다
    @GetMapping("/info")
    public @ResponseBody String info(){
        return "개인정보";
    }
//    @Secured("ROLE_ADMIN")
    @PreAuthorize("hasRole('ADMIN') or hasRole('USER')") //두개의 권한정보를 쓰고 싶을때
    @GetMapping("/admin")
    public @ResponseBody String admin(){
        return "관리자 정보";
    }
}
