package com.example.jwt.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

@Configuration
public class CorsConfig {

    //이 필터를 등록하는 이유는 CORS정책때문에 등록하고 등록을 안하면 컨트롤러에 개별적으로 등록을 안해도 된다
    @Bean
    public CorsFilter corsFilter(){
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();// UrlBasedCorsConfigurationSource source -> CORS 구성을 등록하고 관리 해주는 객체
        CorsConfiguration config = new CorsConfiguration(); // Cors 구성하는 객체를 생성해서
        config.setAllowCredentials(true); // CORS요청을 허용 하겠다라는 뜻 모든 웹설정을 허용하겠다  //밑에 내용들은 어떤요청을 허용할것인지 적은것
        config.addAllowedOrigin("*");   //모든요청은허용
        config.addExposedHeader("*");   //모든헤더허용
        config.addAllowedMethod("*");   //모든메서드허용
        source.registerCorsConfiguration("/api/**",config);// UrlBasedCorsConfigurationSource source -> CORS 구성을 등록하고 관리 해주는  : registerCorsConfiguration-> Cors 구성하는 객체를 등록하는내용  -> /api/** 아래로 오는요청만 CORS를 허용을 해주겠다 라는뜻


        return new CorsFilter(source);
    }
}
