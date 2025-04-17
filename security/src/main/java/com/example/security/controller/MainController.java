package com.example.security.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class MainController {
    // 로그인 페이지를 -> /
    @GetMapping("/") //error라는 매개변수 값을 가져오는것 이값은 필수값은 아니다 넘어오지 않아도 상관 없다라는 설정을 하는것이다
    public String login(@RequestParam(name = "error",required = false)String error,
                        @RequestParam(name = "logout",required = false)String logout,
                        Model model){

        if(error != null){
            model.addAttribute("error",true); //객체 바인딩 작업 error   로그인에 실패하면 알랏창뜨게 설정하려고 하는작업 true로 해준이유는 뷰에서 조건이 걸려있어서 건것
        }
        if(logout != null){
            model.addAttribute("logout",true); //객체 바인딩 작업 error   로그인에 실패하면 알랏창뜨게 설정하려고 하는작업 true로 해준이유는 뷰에서 조건이 걸려있어서 건것
        }
        return "index"; //index.html
    }
}
